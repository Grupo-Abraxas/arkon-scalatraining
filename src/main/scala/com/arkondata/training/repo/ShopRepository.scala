package com.arkondata.training.repo

import cats.effect.Effect
import com.arkondata.training.model.Shop
import doobie.Transactor

import doobie.implicits._



trait ShopRepository[F[_]] {

  def getById(id: Int) : F[ Option[ Shop ] ]
  def getAll(limit: Int, offset: Int): F[ List[ Shop ]]
  def nearbyShops(limit: Int, latitude: Double, longitude: Double): F[ List[ Shop ]]
  def shopsInRadius(radius: Int, latitude: Double, longitude: Double): F[ List[ Shop ]]

}


object ShopRepository {

  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): ShopRepository[ F ] =

    new ShopRepository[ F ] {

      val selectShop = sql""" select id, name, business_name, activity_id, address,
                                              phone_number, email, website, shop_type_id, stratum_id,
                                              ST_X(position::geometry) as long, ST_Y( position ::geometry ) as lat
                                          from  shop """

      def getById(id: Int): F[ Option[ Shop ] ] = {
        val selectShopById = selectShop ++ sql""" where id = $id """
        selectShopById.query[ Shop ].option.transact( xa )
      }

      def getAll(limit: Int, offset: Int): F[ List[Shop] ] = {
        val selectAll = selectShop ++  sql""" order by id limit $limit offset $offset"""
        selectAll.query[ Shop ].to[ List ].transact(xa)
      }

      def nearbyShops(limit: Int, latitude: Double, longitude: Double): F[List[Shop]] = {
        val point = "point(" ++ longitude.toString ++ " "  ++ latitude.toString  ++ ")"
        val selectNearestShops = sql"""
                select id, name, business_name, activity_id, address,
                phone_number, email, website, shop_type_id, stratum_id,
                ST_X(position::geometry) as long, ST_Y( position ::geometry ) as lat,
                    st_distance( position, st_geographyfromtext( $point)  ) as distance
                    from shop  order by distance limit $limit """

        selectNearestShops.query[ Shop ].to[ List ].transact( xa )

      }

      def shopsInRadius(radius: Int, latitude: Double, longitude: Double): F[ List[Shop] ] = {
        val selectyShopsInRadius = sql"""
                    SELECT
                     id, name, business_name, activity_id, address,
                phone_number, email, website, shop_type_id, stratum_id,
                ST_X(position::geometry) as long, ST_Y( position ::geometry ) as lat
                    FROM shop
                    WHERE ST_DWithin( position, ST_MakePoint($longitude,$latitude)::geography, $radius)
                """

        selectyShopsInRadius.query[ Shop ].to[ List ].transact( xa )
      }
    }

}