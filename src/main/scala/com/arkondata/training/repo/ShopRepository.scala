package com.arkondata.training.repo

import cats.effect.Effect.ops.toAllEffectOps
import cats.effect._
import com.arkondata.training.model.{Activity, CreateShopInput, CreateShopPayload, Shop}
import doobie.Transactor
import doobie.implicits._




trait ShopRepository[F[_]] {

  def getById(id: Int) : F[ Option[ Shop ] ]
  def getAll(limit: Int, offset: Int): F[ List[ Shop ]]
  def nearbyShops(limit: Int, latitude: Double, longitude: Double): F[ List[ Shop ]]
  def shopsInRadius(radius: Int, latitude: Double, longitude: Double): F[ List[ Shop ]]
  def createShop(input: CreateShopInput): F[ CreateShopPayload ]

}


object ShopRepository {

  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): ShopRepository[ F ] =

    new ShopRepository[ F ] {

        val activityRepository: ActivityRepository[ F ] = ActivityRepository.fromTransactor( xa )
        val stratumRepository: StratumRepository[ F ] = StratumRepository.fromTransactor( xa )
        val shopTypeRepository: ShopTypeRepository[ F ] = ShopTypeRepository.fromTransactor( xa )

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
          val point = "point(" ++ latitude.toString ++ " "  ++ longitude.toString  ++ ")"
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
                      WHERE ST_DWithin( position, ST_MakePoint($latitude,$longitude)::geography, $radius)
                  """

          selectyShopsInRadius.query[ Shop ].to[ List ].transact( xa )
        }

        def createShop(input: CreateShopInput): F[ CreateShopPayload ]  = {

          val activity =  activityRepository.getOrCreate( input.activity ).toIO.unsafeRunSync
          val stratum = stratumRepository.getOrCreate( input.stratum ).toIO.unsafeRunSync
          val shopType = shopTypeRepository.getOrCreate( input.shopType ).toIO.unsafeRunSync

          create( input, shopType.id, activity.id, stratum.id )
        }



        def create(input: CreateShopInput, idShopType: Int, idActivity: Int, idStratum: Int ): F[ CreateShopPayload ] = {

          val name = input.name
          val businessName = input.businessName
          val address = input.address
          val phoneNumber = input.phoneNumber
          val email = input.email
          val webSite = input.website
          val long = input.long
          val lat = input.lat

          val insertShop = sql"""
              insert into shop ( name, business_name, activity_id, stratum_id, address, phone_number, email,
                                website, shop_type_id, position )
              values ( $name, $businessName, $idActivity, $idStratum, $address, $phoneNumber, $email,
                                  $webSite, $idShopType,
                      sT_SetSRID( ST_POINT( $long, $lat), 4326)::geography) returning id
               """

          insertShop.query[CreateShopPayload].unique.transact( xa )

        }
    }

}