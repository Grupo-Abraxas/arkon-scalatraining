package app

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

case class DatabaseConfig(driver: String, url: String, user: String, password: String)

object Setup {

  def transactor: Resource[IO, HikariTransactor[IO]] = {
    val dbUrl      = s"jdbc:postgresql://${sys.env.getOrElse("POSTGRES_HOST", "postgres")}:${sys.env.getOrElse("POSTGRES_PORT", "5432")}/${sys.env.getOrElse("POSTGRES_DB", "wifi_db")}"
    val dbUser     = sys.env.getOrElse("POSTGRES_USER", "postgres")
    val dbPassword = sys.env.getOrElse("POSTGRES_PASSWORD", "password")

    println(s"Database URL: $dbUrl")
    println(s"Database User: $dbUser")
    println(s"Database Password: [REDACTED]")

    for {
      connectEC <- ExecutionContexts.cachedThreadPool[IO]
      transactor <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        dbUrl,
        dbUser,
        dbPassword,
        connectEC
      )
    } yield transactor
  }
}
