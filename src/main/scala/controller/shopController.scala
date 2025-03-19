package training.controllers

import cats.effect.IO
import training.models._
import training.services.ShopServiceIO
import config.DatabaseConfig
import doobie.Transactor

object ShopController {

  def getShop(id: Int): IO[Option[Shop]] = {
    val shopIO = DatabaseConfig.createTransactor().use { transactor =>
      val shopService = new ShopServiceIO(transactor)
      shopService.getShop(id)
    }
    shopIO
  }

  def getShops(limit: Int, offset: Int): IO[List[Shop]] = {
    val shopsIO = DatabaseConfig.createTransactor().use { transactor =>
      val shopService = new ShopServiceIO(transactor)
      shopService.getShops(limit, offset)
    }
    shopsIO
  }

  def getNearbyShops(lat: Float, long: Float, radius: Int, limit: Int): IO[List[Shop]] = {
    val shopsIO = DatabaseConfig.createTransactor().use { transactor =>
      val shopService = new ShopServiceIO(transactor)
      shopService.getNearbyShops(lat, long, radius, limit)
    }
    shopsIO
  }

  def createShop(shop: Shop): IO[Int] = {
    val createIO = DatabaseConfig.createTransactor().use { transactor =>
      val shopService = new ShopServiceIO(transactor)
      shopService.createShop(shop)
    }
    createIO
  }
}
