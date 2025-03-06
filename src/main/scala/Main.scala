import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.IpLiteralSyntax
import config.DatabaseConfig
import doobie.{ConnectionIO, FC}
import doobie.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object Main extends IOApp {

  object GraphQLService {
    val routes = HttpRoutes.of[IO] {
      case GET -> Root => Ok("GraphQL API está funcionando")
    }
  }

  // Instancia de bd
  val xa = DatabaseConfig.createTransactor

  val connectionTest: ConnectionIO[Unit] = for {
    isAlive <- FC.isValid(1)

    // Obtiene información sobre la conexión actual
    currentDb <- sql"SELECT current_database()".query[String].unique
    userName <- sql"SELECT current_user".query[String].unique
    serverVersion <- sql"SHOW server_version".query[String].unique
    allDatabases <- sql"SELECT datname FROM pg_database WHERE datistemplate = false".query[String].to[List]

    // Imprime información
    //hacer por IO
    _ <- FC.delay(println(s"Conexión establecida: $isAlive"))
    _ <- FC.delay(println(s"Base de datos actual: $currentDb"))
    _ <- FC.delay(println(s"Usuario actual: $userName"))
    _ <- FC.delay(println(s"Versión de PostgreSQL: $serverVersion"))
    _ <- FC.delay(println(s"Todas las bases de datos: ${allDatabases.mkString(", ")}"))
  } yield ()

  // Configura el servicio HTTP
  val httpApp = Router(
    "/graphql" -> GraphQLService.routes
  ).orNotFound

  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- IO.println("Verificando conexión a la base de datos...")
      _ <- connectionTest.transact(xa)  // Ahora debería funcionar
      _ <- IO.println("Iniciando servidor en http://localhost:8080/graphql")
      result <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield result
  }
}