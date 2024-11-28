package controllers

import cats.effect.IO
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.io._
import sangria.execution._
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import sangria.schema._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class GraphQLController(schema: Schema[Unit, Unit])(implicit ec: ExecutionContext) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("GraphQL Server is running")

    case req @ POST -> Root =>
      req.decodeJson[Json].flatMap {
        json =>
          json.hcursor.get[String]("query") match {
            case Right(query) =>
              QueryParser.parse(query) match {
                case Success(queryAst) =>
                  IO.fromFuture(IO {
                    Executor.execute(schema, queryAst)
                  }).flatMap(
                    jsonResult => Ok(jsonResult)
                  ).handleErrorWith(
                    e => InternalServerError(e.getMessage)
                  )

                case Failure(error) =>
                  BadRequest(s"Invalid query: ${error.getMessage}")
              }

            case Left(_) =>
              BadRequest("Missing 'query' field in the request body")
          }
      }
  }
}
