package training.repository

import cats._
import cats.effect._

import training.models.{Shop, ShopInput}
import database.Services

class Repo {
    def shop(id: Long): IO[Option[Shop]] = Services.findShopById(id)

    def listShops(limit: Int, offset: Int): IO[List[Shop]] = Services.listShops(limit, offset)

    def nearbyShops(limit: Int, lat: Double, lng: Double): IO[List[Shop]] = Services.nearbyShops(limit, lat, lng)

    def shopsInRadius(radius: Int, lat: Double, lng: Double): IO[List[Shop]] = Services.shopsInRadius(radius, lat, lng)

    def insertShop(shopInput: ShopInput): IO[Shop] = Services.insertShop(shopInput)
}
