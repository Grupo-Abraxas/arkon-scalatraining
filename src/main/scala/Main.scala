import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze._
import service.GraphqlService

object Main extends IOApp {
    override def run(args: List[String]): IO[ExitCode] = {
      BlazeServerBuilder[IO]
        .bindHttp(8888, "0.0.0.0")
        .withHttpApp(GraphqlService.routes.orNotFound)
        .serve
        .compile
        .lastOrError
    }
}
