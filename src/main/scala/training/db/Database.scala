package training.db

import cats._
import cats.data._
import cats.effect._
import cats.implicits._

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts

import training.models.{Product}
import training.db.Query

object Database {
    val xa = Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql:training", "postgres", "secret")

    def find(id: Int): IO[Option[Product]] =
        Query.find(id).transact(xa)

    def findAll(): IO[List[Product]] =
        Query.findAll.transact(xa)

    def addProduct(name: String, description: String): IO[Product] =
        Query.addProduct(name, description).transact(xa)
}
