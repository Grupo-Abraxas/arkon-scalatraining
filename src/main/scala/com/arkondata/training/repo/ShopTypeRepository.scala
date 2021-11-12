package com.arkondata.training.repo

import cats.effect.Effect
import cats.implicits.toFlatMapOps
import com.arkondata.training.model.TypeShop
import doobie.util.transactor.Transactor
import doobie.implicits._


trait ShopTypeRepository[ F [_] ] {

  def getById(id: Int): F[ TypeShop ]
  def getByName(name: String): F[ Option[TypeShop] ]
  def create(name: String): F[ TypeShop ]
  def getOrCreate( name: String): F[ TypeShop ]
}


object ShopTypeRepository {

  def fromTransactor[ F[_]: Effect ] (xa : Transactor[ F ] ): ShopTypeRepository[ F ] =

    new ShopTypeRepository[F] {

       def getById(id: Int): F[ TypeShop ] = {
         val selectShopTypeById = sql""" select * from shop_type where id = $id """
         selectShopTypeById.query[ TypeShop ].unique.transact( xa )
       }

      def getByName(name: String) : F[ Option [ TypeShop ] ] = {
        val selectActivity = sql""" select id, name from shop_type where name=$name limit 1"""
        selectActivity.query[ TypeShop ].option.transact( xa )
      }

      def create(name: String): F[ TypeShop ] = {
        val insertActivitySql = sql""" insert into shop_type (name) values ( $name ) returning id, name """
        insertActivitySql.query[ TypeShop ].unique.transact( xa )
      }

      def getOrCreate(name: String): F[ TypeShop ] = {

        getByName( name ).flatMap {
          case Some ( typeShop ) => getById( typeShop.id )
          case None =>  create( name )
        }
      }

    }
}
