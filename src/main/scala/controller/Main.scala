package controller

import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze._
import org.http4s.dsl.io._
import org.http4s._
import org.http4s.implicits._
import graphql.SangriaGraphql
import graphql.SangriaGraphql.estadoSchema
import model.Estado
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import repository.EstadoRepo.findAllEstado

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
    case GET -> Root / "estados" => Ok(findAllEstado().asJson)

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
