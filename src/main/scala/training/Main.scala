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
      for {
        request <- req.as[GraphQLRequest]
        result <- IO.fromFuture(IO {
          println("Request: " + request)
          QueryParser.parse(request.query) match {
            case Success(queryAst) =>
              Executor.execute(
                schema = GraphQLSchema.schema,
                queryAst = queryAst,
                variables = request.variables.getOrElse(Json.obj()),
                operationName = request.operationName
              ).recover {
                case error: QueryAnalysisError => error.resolveError
                case error: ErrorWithResolver  => error.resolveError
              }
            case Failure(error: SyntaxError) =>
              println("Syntax Error: " + error.getMessage)
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
              println(Json.obj("error" -> Json.fromString(error.getMessage)))
              Future.successful(Json.obj("error" -> Json.fromString(error.getMessage)))
          }
        })
        resp <- Ok(result)
      } yield resp
  }

  val app: HttpApp[IO] = Router(
    "/" -> graphQLRoute,
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root => Ok("GraphQL Server Running")
    }
  ).orNotFound

  override def run: IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"5000")
      .withHttpApp(app)
      .build
      .use(_ => IO.never)
  }
}