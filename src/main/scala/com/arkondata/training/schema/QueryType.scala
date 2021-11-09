

package com.arkondata.training.schema

import cats.effect._
import cats.effect.implicits._
import com.arkondata.training.repo.MasterRepository
import sangria.schema._

object QueryType {

  val IDType: Argument[Int] = Argument( "id", IntType )
  val OffsetType: Argument[Int] = Argument( "offset", IntType )
  val LimitType: Argument[Int] = Argument( "limit", IntType, defaultValue = 50 )

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
          arguments   = List( OffsetType, LimitType),
          description = Some( "Get shops  "),
          resolve     = c => c.ctx.shopRepository.getAll( c.arg( LimitType ), c.arg( OffsetType ) ).toIO.unsafeToFuture
        ),


      )
    )

  def schema[F[_]: Effect]: Schema[MasterRepository[F], Unit] = Schema(QueryType[F])

}
