package training.repository

import cats._
import cats.effect._

import doobie.hikari.HikariTransactor

import training.models.{Shop, ShopInput}
import database.Services

/** Repositorio.
 *
 *  @constructor crea el Repositorio
 *  @param db transactor de base de datos
 */
class Repo(db: HikariTransactor[IO]) {
    val service = new Services(db)

    def shop(id: Long): IO[Option[Shop]] = service.findShopById(id)

    def listShops(limit: Int, offset: Int): IO[List[Shop]] = service.listShops(limit, offset)

    def nearbyShops(limit: Int, lat: Double, lng: Double): IO[List[Shop]] = service.nearbyShops(limit, lat, lng)

    def shopsInRadius(radius: Int, lat: Double, lng: Double): IO[List[Shop]] = service.shopsInRadius(radius, lat, lng)

    def insertShop(shopInput: ShopInput): IO[Shop] = service.insertShop(shopInput)
}
