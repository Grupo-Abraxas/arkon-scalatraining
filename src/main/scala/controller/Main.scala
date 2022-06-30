package controller

import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import io.circe.{Json, JsonObject}
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze._
import org.http4s.dsl.io._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.implicits._
import graphql.SangriaGraphql
import model.Estado
import org.http4s.FormDataDecoder.formEntityDecoder
import scala.concurrent.Future
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import repository.EstadoRepo
import sangria.parser.QueryParser
import scala.util.{Failure, Success}


object Main extends IOApp{

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:mtbMX",
    "userApp",
    "userAppPs"
  )

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }.orNotFound

  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "length" / str => Ok(str.length.toString)
    case GET -> Root / "hello" / name => Ok(s"Hello, $name.")
    case GET -> Root / "estados" => Ok(new EstadoRepo().findAllEstado().asJson)
    case GET -> Root / "estado" / IntVar(id) => Ok(new EstadoRepo().findEstadoById(id).asJson)
    /*case request @ POST -> Root / "graphql" ⇒
      for {
        jsonObject <- request.as[Json]
        resp <-  Ok("saludos!".concat(jsonObject.toString()) )
      } yield resp*/
    case request @ POST -> Root / "graphql" =>
      request.as[Json].flatMap{ jsonBody =>
        val query = jsonBody.findAllByKey("query")
        val operationName = jsonBody.findAllByKey("operationName")
        val variables = jsonBody.findAllByKey("variables")
        println(query)
        println(operationName)
        println(variables)

        Ok("saludos!".concat(jsonBody.toString()) )
        //IO.fromFuture(IO["hola"]).flatten
      }
  }



  val app: Kleisli[IO, Request[IO], Response[IO]] = Router(
    "/" -> route
  ).orNotFound


  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .useForever
      .as(ExitCode.Success)
}
