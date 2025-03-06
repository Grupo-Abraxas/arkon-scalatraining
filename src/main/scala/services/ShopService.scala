package services

import cats.effect.IO
import models.Shop
import queries.{ActivityQueries, ShopRepository, ShopTypeQueries, StratumQueries}

class ShopService(shopRepository: ShopRepository,
                  activityRepository: ActivityQueries,
                  stratumRepository: StratumQueries,
                  shopTypeRepository: ShopTypeQueries
                   ){

  //logica para crear una shop
  def createShop(shop: Shop): IO[Shop] = {
    shopRepository.create(shop)
  }

}
