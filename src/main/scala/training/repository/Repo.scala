package training.repository

import cats._
import cats.effect._

import training.models.{Shop, Product}
import database.Services

class Repo {
    def shop(id: Long): IO[Option[Shop]] = Services.findShopById(id)

    // def products: IO[List[Product]] = ProductService.findAll()

    // def addProduct(name: String, description: String): IO[Product] = ProductService.addProduct(name, description)
}
