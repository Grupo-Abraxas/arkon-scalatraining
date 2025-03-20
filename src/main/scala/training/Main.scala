import cats.effect.{IO, IOApp}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.dsl.io._
import org.http4s.implicits._
import com.comcast.ip4s._
import training.graphql.GraphQLSchema
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.circe._
import sangria.parser.{QueryParser, SyntaxError}
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import cats.syntax.all._
import scala.util.{Failure, Success}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp.Simple {

  case class GraphQLRequest(query: String, variables: Option[Json] = None, operationName: Option[String] = None)

  val graphQLRoute = HttpRoutes.of[IO] {
    case req @ POST -> Root / "graphql" =>
      println(s"Received GraphQL request: ${req.method} ${req.uri}")
      
      for {
        request <- req.as[GraphQLRequest].attempt.flatMap {
          case Right(request) => 
            println(s"Parsed request successfully: ${request.query}")
            println(s"Variables: ${request.variables}")
            println(s"Operation name: ${request.operationName}")
            IO.pure(request)
          case Left(error) =>
            println(s"Failed to parse request: ${error.getMessage}")
            IO.raiseError(error)
        }
        
        result <- IO.fromFuture(IO {
          println("Starting query execution")
          QueryParser.parse(request.query) match {
            case Success(queryAst) =>
              println(s"Successfully parsed query: ${queryAst}")
              println("Executing query with Sangria")
              
              Executor.execute(
                schema = GraphQLSchema.schema,
                queryAst = queryAst,
                variables = request.variables.getOrElse(Json.obj()),
                operationName = request.operationName
              ).map { result =>
                println(s"Query executed successfully: ${result.spaces2}")
                result
              }.recover {
                case error: QueryAnalysisError => 
                  println(s"Query analysis error: ${error.getMessage}")
                  error.resolveError
                case error: ErrorWithResolver =>
                  println(s"Error with resolver: ${error.getMessage}")
                  error.resolveError
                case error: Throwable =>
                  println(s"Unexpected error during execution: ${error.getMessage}")
                  error.printStackTrace()
                  Json.obj("error" -> Json.fromString(s"Unexpected error: ${error.getMessage}"))
              }
              
            case Failure(error: SyntaxError) =>
              println(s"Syntax Error: ${error.getMessage}")
              Future.successful(
                Json.obj(
                  "syntaxError" -> Json.fromString(error.getMessage),
                  "locations" -> Json.arr(
                    Json.obj(
                      "line" -> Json.fromInt(error.originalError.position.line),
                      "column" -> Json.fromInt(error.originalError.position.column)
                    )
                  )
                )
              )
            case Failure(error) =>
              println(s"General error: ${error.getMessage}")
              error.printStackTrace()
              Future.successful(Json.obj("error" -> Json.fromString(error.getMessage)))
          }
        })
        
        _ = println(s"Sending response: ${result.spaces2}")
        resp <- Ok(result).attempt.flatMap {
          case Right(response) => 
            println("Response sent successfully")
            IO.pure(response)
          case Left(error) =>
            println(s"Failed to send response: ${error.getMessage}")
            IO.raiseError(error)
        }
      } yield resp
  }

  val app: HttpApp[IO] = Router(
    "/" -> graphQLRoute,
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root => 
        println("Healthcheck endpoint called")
        Ok("GraphQL Server Running")
    }
  ).orNotFound

  override def run: IO[Unit] = {
    println("Starting GraphQL server")
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"5000")
      .withHttpApp(app)
      .build
      .use { server =>
        println(s"Server started at ${server.address}")
        IO.never
      }
  }
}