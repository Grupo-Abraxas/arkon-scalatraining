package com.arkondata.training.repo

import cats.effect.Effect
import com.arkondata.training.model.Shop
import doobie.Transactor

import doobie.implicits._



trait ShopRepository[F[_]] {

  def getById(id: Int) : F[ Option[ Shop ] ];

}


object ShopRepository {

  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): ShopRepository[ F ] =

    new ShopRepository[ F ] {

      def getById(id: Int): F[ Option[ Shop ] ] = {
        val selectShopById =  sql"""
              select id, name, business_name, activity_id, address,
                  phone_number, email, website, shop_type_id, stratum_id,
                  ST_X(position::geometry) as long, ST_Y( position ::geometry ) as lat
              from shop where id = $id
                    """
        selectShopById.query[ Shop ].option.transact( xa )
      }


    }

}