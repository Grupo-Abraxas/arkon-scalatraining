package training.repository

import cats._
import cats.effect._

import training.models.{Product}
import training.db.Database

class Repo {
    def product(id: Int): IO[Option[Product]] = Database.find(id)

    def products: IO[List[Product]] = Database.findAll()

    def addProduct(name: String, description: String): IO[Product] = Database.addProduct(name, description)
}
