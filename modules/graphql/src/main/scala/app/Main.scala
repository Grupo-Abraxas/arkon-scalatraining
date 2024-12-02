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

  private def env(key: String, default: String): IO[String] =
    IO(sys.env.getOrElse(key, default))

  override def run: IO[Unit] = {
    transactorResource.use { transactor =>
      for {

        dbHost     <- env("POSTGRES_HOST", "postgres")
        dbPort     <- env("POSTGRES_PORT", "5432")
        dbName     <- env("POSTGRES_DB", "wifi_db")
        dbUser     <- env("POSTGRES_USER", "postgres")
        dbPassword <- env("POSTGRES_PASSWORD", "password")

        dbUrl = s"jdbc:postgresql://$dbHost:$dbPort/$dbName"

        _ <- IO.println(s"Database URL: $dbUrl")
        _ <- IO.println(s"Database User: $dbUser")

        _ <- FlywaySetup.migrate(
          databaseUrl = dbUrl,
          user = dbUser,
          password = dbPassword
        )

        wifiPointRepository = new WifiPointRepository(transactor)
        wifiPointService    = new WifiPointService(wifiPointRepository)
        queryController     = new QueryController(wifiPointService)
        mutationController  = new MutationController(wifiPointService)
        schemaDefinition    = new SchemaDefinition(queryController, mutationController)

        app = Router(
          "/graphql" -> new GraphQLController(schemaDefinition.schema).routes
        ).orNotFound

        _ <- EmberServerBuilder
          .default[IO]
          .withHost(
            Host.fromString("0.0.0.0").getOrElse(throw new IllegalArgumentException("Invalid host"))
          )
          .withPort(port"8080")
          .withHttpApp(app)
          .build
          .useForever
      } yield ()
    }
  }
}
