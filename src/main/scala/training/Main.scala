import cats.effect.{IO, IOApp}
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.dsl.io._
import org.http4s.implicits._
import com.comcast.ip4s._
import org.http4s.server.middleware.Logger

object Main extends IOApp.Simple {

  val app: HttpApp[IO] = Router(
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root => Ok("Hello World")
    }
  ).orNotFound

  override def run: IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"5000")
      .withHttpApp(app)
      .build
      .use(_ => IO.never)
  }
}
