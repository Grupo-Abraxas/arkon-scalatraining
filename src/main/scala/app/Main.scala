package app

import controllers.GraphQLController
import cats.effect.{IO, IOApp}
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import scala.concurrent.ExecutionContext

object Main extends IOApp.Simple {
  implicit val ec: ExecutionContext = ExecutionContext.global

  // Llmar a Setup

  // Configura el controlador GraphQL
  val controller = new GraphQLController

  // Crea la aplicación con el controlador
  val app = Router("/" -> controller.routes).orNotFound

  // Configura y ejecuta el servidor
  override def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("localhost").getOrElse(ip"127.0.0.1"))
      .withPort(port"8080")
      .withHttpApp(app)
      .build
      .useForever
}
