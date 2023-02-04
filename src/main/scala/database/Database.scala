package database

import cats._
import cats.effect._

import doobie.{Transactor}

object Database {
    val tx = Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql:inegi", "postgres", "secret")
}