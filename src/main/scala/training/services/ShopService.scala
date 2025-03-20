package training.services

import training.models._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.effect.IO


class ShopServiceIO(transactor: Transactor[IO]) {

  def getShop(id: Int): IO[Option[Shop]] = {
    ShopQueries.getShop(id)
      .transact(transactor)
  }

  def getShops(limit: Int, offset: Int): IO[List[Shop]] = {
    ShopQueries.getShops(limit, offset)
      .transact(transactor)
  }

  def getNearbyShops(lat: Float, long: Float, radius: Int, limit: Int): IO[List[Shop]] = {
    ShopQueries.getNearbyShops(lat, long, radius, limit)
      .transact(transactor)
  }

  def createShop(shop: Shop): IO[Int] = {
    ShopQueries.createShop(shop)
      .transact(transactor)
  }
}