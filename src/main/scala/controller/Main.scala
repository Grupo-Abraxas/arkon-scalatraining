package controller

import cats.data.Kleisli

import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s.server.{Router}
import org.http4s.server.blaze._
import org.http4s._
import org.http4s.implicits._
import graphql.{SangriaGraphExec}


object Main extends IOApp{

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:mtbMX",
    "userApp",
    "userAppPs"
  )



  val app: Kleisli[IO, Request[IO], Response[IO]] = Router(
    "/" -> SangriaGraphExec.route
  ).orNotFound


  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .useForever
      .as(ExitCode.Success)
}
