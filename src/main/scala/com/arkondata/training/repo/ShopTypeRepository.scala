package com.arkondata.training.repo

import cats.effect.Effect
import com.arkondata.training.model.TypeShop
import doobie.util.transactor.Transactor
import doobie.implicits._


trait ShopTypeRepository[ F [_] ] {

  def getById(id: Int): F[ TypeShop ]
}


object ShopTypeRepository {

  def fromTransactor[ F[_]: Effect ] (xa : Transactor[ F ] ): ShopTypeRepository[ F ] =

    new ShopTypeRepository[F] {

       def getById(id: Int): F[ TypeShop ] = {
         val selectShopTypeById = sql""" select * from shop_type where id = $id """
         selectShopTypeById.query[ TypeShop ].unique.transact( xa )
       }

    }
}
