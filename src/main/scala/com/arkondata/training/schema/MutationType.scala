package com.arkondata.training.schema

import cats.effect.Effect.ops.toAllEffectOps
import cats.effect._
import com.arkondata.training.model.CreateShopInput
import com.arkondata.training.repo.MasterRepository
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.sprayJson.sprayJsonReaderFromInput
import sangria.schema._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

object MutationType {



  implicit val shopFormat: RootJsonFormat[ CreateShopInput ] = jsonFormat12( CreateShopInput )
  lazy val ValueShopInputType: InputType[ CreateShopInput ] = deriveInputObjectType[ CreateShopInput ]( )

  val ShopArg: Argument[ CreateShopInput ] = Argument( "input", ValueShopInputType )


  def apply[F[_]: Effect ]: ObjectType[MasterRepository[F], Unit] =
    ObjectType(
      name  = "Mutation",
      fields = fields(

        Field(
          name        = "createShop",
          fieldType   = CreateShopPayloadType[ F ],
          arguments   = ShopArg :: Nil,
          description = Some( " create shop " ),
          resolve     = c => c.ctx.shopRepository.createShop( c.arg( ShopArg ) ).toIO.unsafeToFuture
        ),

      )
    )

}
