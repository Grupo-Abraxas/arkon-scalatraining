package database

import cats.effect._
import cats.implicits._

import doobie._
import doobie.implicits._
import doobie.hikari._

object Database {
    // val tx = Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql:inegi", "postgres", "secret")
    val tx: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO]("org.postgresql.Driver", "jdbc:postgresql:inegi", "postgres", "secret", ce)
    } yield xa
}