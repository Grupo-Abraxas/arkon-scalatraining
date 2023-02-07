package training.controller

import cats.effect._

import scala.concurrent.ExecutionContext.global

import io.circe.generic.auto._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeServerBuilder

import database.Database.{transactor}
import training.models.{RequestJson}
import training.graphql.Executor

object Main extends IOApp {
  implicit val decoder = jsonOf[IO, RequestJson]

  val app = HttpRoutes.of[IO] {
    case request @ POST -> Root / "graphql" =>
      transactor.use { xa =>
        for {
          requestJson <- request.as[RequestJson]
          response <- Executor.execute(xa, requestJson)
        } yield response
      }
    case request @ GET -> Root =>
      StaticFile.fromResource("/assets/graphiql.html", Some(request)).getOrElseF(NotFound())
  }

  def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(app.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
