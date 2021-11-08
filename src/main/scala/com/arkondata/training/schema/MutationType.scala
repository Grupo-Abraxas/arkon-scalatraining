package com.arkondata.training.schema

import cats.effect._
import com.arkondata.training.model.Shop
import com.arkondata.training.repo.MasterRepo
import sangria.macros.derive.deriveInputObjectType

import sangria.marshalling.sprayJson.sprayJsonReaderFromInput
import sangria.schema._
import spray.json.DefaultJsonProtocol._

object MutationType {


  implicit val shopFormat = jsonFormat12( Shop )
  lazy val ShopInputType: InputType[Shop] = deriveInputObjectType[ Shop ]( )

  val ShopArg = Argument("shop", ShopInputType )


  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Unit] =
    ObjectType(
      name  = "Mutation",
      fields = fields(

        Field(
          name        = "createShop",
          fieldType   = StringType,
          arguments   = ShopArg :: Nil,
          description = Some("Update the specified Country, if it exists."),
          resolve     = c => c.ctx.inegi.insert( c.arg( ShopArg ) )
        ),

      )
    )

}
