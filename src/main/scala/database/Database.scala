package database

import cats.effect._
import cats.implicits._

import doobie._
import doobie.implicits._
import doobie.hikari._

object Database {
    val xa = Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql:inegi", "postgres", "secret")
    
    val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO]("org.postgresql.Driver", "jdbc:postgresql:inegi", "postgres", "secret", ce)
    } yield xa
}