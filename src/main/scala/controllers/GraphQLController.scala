package controllers

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.Json
import sangria.execution._
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import sangria.schema._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class GraphQLController(implicit ec: ExecutionContext) {
  // Define el tipo de consulta GraphQL
  private val QueryType: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field("hello", StringType, resolve = _ => "GraphQL Server is running")
    )
  )

  // Define el esquema GraphQL
  private val schema: Schema[Unit, Unit] = Schema(QueryType)

  // Define las rutas HTTP
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    // Ruta de prueba para GET
    case GET -> Root =>
      Ok("GraphQL Server is running")

    // Ruta para manejar consultas GraphQL en formato estándar
    case req @ POST -> Root =>
      // Decodifica la solicitud como JSON
      req.decodeJson[Json].flatMap { json =>
        // Extrae la consulta desde el campo "query" en el JSON
        json.hcursor.get[String]("query") match {
          case Right(query) =>
            // Intenta parsear la consulta GraphQL
            QueryParser.parse(query) match {
              case Success(queryAst) =>
                // Ejecuta la consulta usando el esquema
                IO.fromFuture(IO {
                    Executor.execute(schema, queryAst)
                  }).flatMap(jsonResult => Ok(jsonResult))
                  .handleErrorWith(e => InternalServerError(e.getMessage))

              case Failure(error) =>
                // Devuelve error si el parseo falla
                BadRequest(s"Invalid query: ${error.getMessage}")
            }

          case Left(_) =>
            // Devuelve error si no se encuentra el campo "query" en el JSON
            BadRequest("Missing 'query' field in the request body")
        }
      }
  }
}
