package repositories

import cats.effect.IO
import doobie.Transactor
import models.Shop

class ShopRepository (transactor: Transactor[IO]){

  def findById(id : String) : Unit = {

  } : Unit

}
