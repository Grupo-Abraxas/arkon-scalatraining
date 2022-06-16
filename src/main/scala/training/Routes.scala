package training

import io.circe.Json
import Repo.ProductRepo
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonDecoder
import org.http4s.dsl._
import sangria.execution.Executor
import sangria.macros.LiteralGraphQLStringContext
import schemas.QueryType
import sangria.marshalling.circe._
import sangria.schema.Schema

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import sangria.schema._
import sangria.execution._
import sangria.macros._
import sangria.marshalling.circe._

import scala.concurrent.ExecutionContext.Implicits.global



object Routes {

  def apiRoutes[F[_]: Sync](H: ApiRes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "api" / "status" =>
        for {
          message <- H.apiMessage()
          resp <- Ok(message)
        } yield resp
    }
  }

  def apiGraphqlRoutes[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "graphql" =>{
        val QueryType = ObjectType("Query", fields[Unit, Unit](
          Field("consult", StringType, resolve = _ => "Bienvenidos API Graphql")
        ))

        val schema = Schema(QueryType)

        val query = graphql"{ consult }"

        val result = Executor.execute(schema, query)

        result.foreach(res => println(res))

        Ok()
      }
        /**req.as[Json].flatMap(graphQL.query).flatMap {
          case Right(json) => Ok(json)
          case Left(json)  => BadRequest(json)
        }**/
    }
  }
}
