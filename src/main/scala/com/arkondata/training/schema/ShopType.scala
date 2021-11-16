package com.arkondata.training.schema

import cats.effect._
import cats.effect.implicits._
import com.arkondata.training.model.Shop
import com.arkondata.training.repo.MasterRepository
import com.arkondata.training.schema.QueryType.{InRadiusType, LimitNearbyShopsType}
import sangria.schema.{Field, FloatType, IntType, ListType, ObjectType, StringType, fields}

object  ShopType {


  def apply[F[_]: Effect]: ObjectType[ MasterRepository[F], Shop ] =
    ObjectType(
      name     = "Shop",

      fieldsFn = () => fields (

        Field( "id", IntType, resolve = _.value.id ),
        Field( "name", StringType, resolve = _.value.name ),
        Field( "businessName", StringType, resolve = _.value.businessName ),
        Field( "activity", ActivityType[F], resolve = c => c.ctx.activityRepository.getById( c.value.activityId ).toIO.unsafeToFuture() ),
        Field( "address", StringType, resolve = _.value.address ),
        Field( "phoneNumber", StringType, resolve = _.value.phoneNumber ),
        Field( "email", StringType, resolve = _.value.email ),
        Field( "website", StringType, resolve = _.value.website ),
        Field( "shopType", TypeShopType[ F ], resolve = c => c.ctx.shopTypeRepository.getById( c.value.shopTypeId ).toIO.unsafeToFuture() ),
        Field( "stratum", StratumType[ F ], resolve =  c => c.ctx.stratumRepository.getById( c.value.stratumId ).toIO.unsafeToFuture() ),
        Field( "lat", FloatType, resolve = _.value.lat ),
        Field( "long", FloatType, resolve = _.value.long ),
        Field(
          name        = "nearbyShops",
          fieldType   = ListType( ShopType[F] ),
          arguments   = List(  LimitNearbyShopsType ),
          resolve     = c => c.ctx.shopRepository.nearbyShops( c.arg( LimitNearbyShopsType), c.value.lat, c.value.long ).toIO.unsafeToFuture
        ),
        Field(
          name        = "shopsInRadius",
          fieldType   = ListType( ShopType[F] ),
          arguments   = List( InRadiusType ),
          description = Some( "Get shops  "),
          resolve     = c => c.ctx.shopRepository.shopsInRadius( c.arg( InRadiusType), c.value.lat, c.value.long  ).toIO.unsafeToFuture
        )



      )

    )



}
