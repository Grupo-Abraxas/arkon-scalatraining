package services

import cats.effect.IO
import models.Shop
import queries.{ActivityQueries, ShopQueries, ShopTypeQueries, StratumQueries}

class ShopService(shopQueries: ShopQueries,
                  activityQueries: ActivityQueries,
                  stratumQueries: StratumQueries,
                  shopTypeQueries: ShopTypeQueries
                   ){

  //logica para crear una shop
  def createShop(shop: Shop): IO[Shop] = {
    shopQueries.create(shop)
  }

}
