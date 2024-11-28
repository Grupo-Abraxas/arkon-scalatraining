package app

import cats.effect.{IO, IOApp, Resource}
import cats.effect.unsafe.IORuntime
import com.comcast.ip4s._
import controllers.{GraphQLController, MutationController, QueryController, SchemaDefinition}
import doobie.hikari.HikariTransactor
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import repositories.WifiPointRepository
import services.WifiPointService

import scala.concurrent.ExecutionContext

object Main extends IOApp.Simple {

  private implicit val ec: ExecutionContext = ExecutionContext.global
  override protected implicit val runtime: IORuntime = IORuntime.global
  private val transactorResource: Resource[IO, HikariTransactor[IO]] = Setup.transactor

  override def run: IO[Unit] = {
    transactorResource.use {
      transactor =>
        val wifiPointRepository = new WifiPointRepository(transactor)
        val wifiPointService    = new WifiPointService(wifiPointRepository)

        val queryController    = new QueryController(wifiPointService)
        val mutationController = new MutationController(wifiPointService)
        val schemaDefinition = new SchemaDefinition(queryController, mutationController)
        val app = Router(
          "/graphql" -> new GraphQLController(schemaDefinition.schema).routes
        ).orNotFound
        EmberServerBuilder
          .default[IO]
          .withHost(
            Host.fromString("0.0.0.0").getOrElse(throw new IllegalArgumentException("Invalid host"))
          )
          .withPort(port"8080")
          .withHttpApp(app)
          .build
          .useForever
    }
  }
}
