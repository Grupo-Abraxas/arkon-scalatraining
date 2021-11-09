

package com.arkondata.training.schema

import cats.effect._
import cats.effect.implicits._
import com.arkondata.training.repo.MasterRepository
import sangria.schema._

object QueryType {

  val IDType: Argument[Int] = Argument( "id", IntType )
  val OffsetType: Argument[Int] = Argument( "offset", IntType, defaultValue = 0 )

  val LimitShopsType: Argument[Int] = Argument( "limit", IntType, defaultValue = 50 )
  val LimitNearbyShopsType: Argument[Int] = Argument( "limit", IntType, defaultValue = 5 )

  val InRadiusType: Argument[Int] = Argument( "radius", IntType, defaultValue = 50 )

  val LongitudeType: Argument[Double] = Argument( "long", FloatType )
  val LatitudeType: Argument[Double] = Argument( "lat", FloatType )

  def apply[F[_]: Effect]: ObjectType[MasterRepository[F], Unit] =
    ObjectType(
      name  = "Query",
      fields = fields(

        Field(
          name        = "shop",
          fieldType   = OptionType( ShopType[F] ),
          arguments   = IDType :: Nil,
          description = Some( "Get shop by id  "),
          resolve     = c => c.ctx.shopRepository.getById( c.arg( IDType ) ).toIO.unsafeToFuture
        ),

        Field(
          name        = "shops",
          fieldType   = ListType( ShopType[F] ),
          arguments   = List( OffsetType, LimitShopsType),
          description = Some( "Get shops  "),
          resolve     = c => c.ctx.shopRepository.getAll( c.arg( LimitShopsType ), c.arg( OffsetType ) ).toIO.unsafeToFuture
        ),

        Field(
          name        = "nearbyShops",
          fieldType   = ListType( ShopType[F] ),
          arguments   = List( LatitudeType, LongitudeType, LimitNearbyShopsType ),
          description = Some( "Get shops  "),
          resolve     = c => c.ctx.shopRepository.nearbyShops( c.arg( LimitNearbyShopsType), c.arg( LatitudeType ), c.arg( LongitudeType ) ).toIO.unsafeToFuture
        ),

        Field(
          name        = "shopsInRadius",
          fieldType   = ListType( ShopType[F] ),
          arguments   = List( LatitudeType, LongitudeType, InRadiusType ),
          description = Some( "Get shops  "),
          resolve     = c => c.ctx.shopRepository.shopsInRadius( c.arg( LimitNearbyShopsType), c.arg( LatitudeType ), c.arg( LongitudeType ) ).toIO.unsafeToFuture
        ),




      )
    )

  def schema[F[_]: Effect]: Schema[MasterRepository[F], Unit] = Schema(QueryType[F])

}
