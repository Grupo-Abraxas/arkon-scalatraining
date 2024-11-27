package graphql.app

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s._
import doobie.hikari.HikariTransactor
import graphql.controllers.GraphQLController
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router

import scala.concurrent.ExecutionContext

object Main extends IOApp.Simple {
  // Execution context used across the application
  private implicit val ec: ExecutionContext = ExecutionContext.global

  private val transactorResource: Resource[IO, HikariTransactor[IO]] = Setup.transactor

  private val controller: GraphQLController = new GraphQLController

  private val app: org.http4s.HttpApp[IO] = Router("/" -> controller.routes).orNotFound

  override def run: IO[Unit] =
    transactorResource.use { _ =>
      EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString("0.0.0.0").getOrElse(throw new IllegalArgumentException("Invalid host")))
        .withPort(port"8080")
        .withHttpApp(app)
        .build
        .useForever
    }
}