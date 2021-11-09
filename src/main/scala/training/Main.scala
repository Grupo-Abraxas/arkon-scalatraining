package training

import scala.concurrent.ExecutionContext.global

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeServerBuilder


object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
