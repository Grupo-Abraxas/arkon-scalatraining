package graphql

import cats.effect.IO
import graphql.SangriaGraphql.{SchemaEstado}
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io.{->, /, BadRequest, GET, Ok, POST, Root}
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.client.{Client, JavaNetClientBuilder}
import repository.RepoMbMxImpl
import sangria.execution._
import sangria.parser.{QueryParser, SyntaxError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import sangria.marshalling.circe._
import org.http4s.implicits.http4sLiteralsSyntax
import sangria.execution.deferred.DeferredResolver

import java.util.concurrent.Executors


object SangriaGraphExec {
  val blockingPool = Executors.newFixedThreadPool(5)
  val httpClient: Client[IO] = JavaNetClientBuilder[IO].create

  def unidadesDataCdmx(): IO[Json] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=ad360a0e-b42f-482c-af12-1fd72140032e"
    httpClient.expect[Json](target)
  }
  def alcaldiasDataCdmx(): IO[Json] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=e4a9b05f-c480-45fb-a62c-6d4e39c5180e"
    httpClient.expect[Json](target)
  }

  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name => Ok(s"Hello, $name.")
    case GET -> Root / "unidades" =>
     Ok (unidadesDataCdmx)
    case GET -> Root / "alcaldias"=>
      Ok (alcaldiasDataCdmx)
    case request@POST -> Root / "graphql" =>
      request.as[Json].flatMap { jsonBody =>
        val jsonBodyhcursor = jsonBody.hcursor
        val query = jsonBodyhcursor.downField("query").as[String] match {
          case Right(q) => q.toString
          case Left(q) => ""
        }

        println("query : ".concat(query))

        QueryParser.parse(query) match {

          case Success(query) =>
            print("SchemaEstado")
            print(SchemaEstado)

            val result :Future[Json] = Executor.execute(SchemaEstado, query,  new RepoMbMxImpl)
            Try(Await.result(result, 10 seconds)) match {
              case Success(result) => {
                println(result)
                Ok(result)
              }
              case Failure(_) => BadRequest("Failure Happened")
            }

          case Failure(error: SyntaxError) => {
            println("falla")
            BadRequest("erorr!".concat(error.getMessage()))
          }
          case _ => BadRequest("Nada")
        }
      }

  }
}
