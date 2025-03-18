package config

import cats.effect.{IO, Resource}
import doobie.{ExecutionContexts, Transactor}
import doobie.hikari.HikariTransactor

object DatabaseConfig {

  def createTransactor(
    dbName: String,
    user: String,
    password: String,
    host: String
  ): Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    s"jdbc:postgresql://$host:5432/$dbName",
    user,
    password
  )

  def createTransactorResource(
    dbName: String,
    user: String,
    password: String,
    host: String
  ): Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        s"jdbc:postgresql://$host:5432/$dbName",
        user,
        password,
        ce
      )
    } yield xa
}
