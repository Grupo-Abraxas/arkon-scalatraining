package training

import training.config.DatabaseConfig
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  def createServer[F[_] : ConcurrentEffect : ContextShift : Timer]: Resource[F, Server[F]] =
    for {
      blocker <- Blocker[F]
      transactor <- DatabaseConfig.transactor[F](blocker)
      server <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "localhost")
        .resource
    } yield server

  def run(args: List[String]): IO[ExitCode] = {
    createServer[IO].use(_ => IO.never).as(ExitCode.Success)
  }
}
