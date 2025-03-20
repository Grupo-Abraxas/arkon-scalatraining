package training.controllers

import cats.effect.IO
import training.models._
import training.services.ShopServiceIO
import config.DatabaseConfig

object ShopController {

  def getShop(id: Int): IO[Option[Shop]] = {
    DatabaseConfig.createTransactor().use { transactor =>
      new ShopServiceIO(transactor).getShop(id)
    }
  }

  def getShops(limit: Int, offset: Int): IO[List[Shop]] = {
    DatabaseConfig.createTransactor().use { transactor =>
      new ShopServiceIO(transactor).getShops(limit, offset)
    }
  }

  def getNearbyShops(lat: Float, long: Float, radius: Int, limit: Int): IO[List[Shop]] = {
    DatabaseConfig.createTransactor().use { transactor =>
      new ShopServiceIO(transactor).getNearbyShops(lat, long, radius, limit)
    }
  }

  def createShop(shop: Shop): IO[Int] = {
    println(shop)
    DatabaseConfig.createTransactor().use { transactor =>
      new ShopServiceIO(transactor).createShop(shop)
    }
  }
}
