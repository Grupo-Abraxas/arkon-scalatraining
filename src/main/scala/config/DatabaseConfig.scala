package config

import cats.effect.{IO, Resource}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

object DatabaseConfig {

  def createTransactor: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",             // driver
    "jdbc:postgresql://localhost:5432/shops", // url
    "postgres",                          // usuario
    "postgres"                           // contraseña
  )

  def createTransactorResource: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/shops_db",
        "postgres",
        "postgres",
        ce
      )
    } yield xa
}
