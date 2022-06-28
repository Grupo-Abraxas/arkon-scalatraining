package controller

import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s.server.Server
import org.http4s.server.blaze._
import org.http4s.dsl._
import org.http4s._
import org.http4s.implicits._
import cats.effect._
import cats.implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._

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



  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .resource

  }
}
