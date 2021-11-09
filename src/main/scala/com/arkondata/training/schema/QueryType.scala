

package com.arkondata.training.schema

import cats.effect._
import cats.effect.implicits._
import com.arkondata.training.repo.MasterRepository
import sangria.schema._

object QueryType {

  val IDType: Argument[Int] = Argument( "id", IntType )

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


      )
    )

  def schema[F[_]: Effect]: Schema[MasterRepository[F], Unit] = Schema(QueryType[F])

}
