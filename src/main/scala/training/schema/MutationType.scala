// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package training.schema

import cats.effect.Effect
import cats.effect.implicits._
import io.circe.generic.auto._
import sangria.macros.derive._
import sangria.marshalling.circe._
import sangria.schema._
import training.model.{CreatedPayload, Shop}
import training.repo.MasterRepo

object MutationType {
  val CreateShopInput: InputObjectType[Shop] =
    deriveInputObjectType[Shop](
      InputObjectTypeName("CreateShopInput")
    )

  val CreateShopInputArg: Argument[Shop] = Argument("input", CreateShopInput)

  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Unit] =
    ObjectType(
      name = "Mutation",
      fields = fields(
        Field(
          name = "createShop",
          fieldType = CreateShopPayloadType[F],
          arguments = List(CreateShopInputArg),
          resolve = c => {
            val activity = c.ctx.activity
              .fetchOrCreateByName(c.arg(CreateShopInputArg).activity)
              .toIO
              .unsafeRunSync()

            val shopType = c.ctx.shopType
              .fetchOrCreateByName(c.arg(CreateShopInputArg).shopType)
              .toIO
              .unsafeRunSync()

            val stratum = c.ctx.stratum
              .fetchOrCreateByName(c.arg(CreateShopInputArg).stratum)
              .toIO
              .unsafeRunSync()

            val shopData =
              c.arg(CreateShopInputArg)
                .copy(
                  activity = Option(activity.id),
                  shopType = Option(shopType.id),
                  stratum = Option(stratum.id)
                )

            val createdPayload = c.ctx.shop.create(shopData).toIO.unsafeRunSync
            CreatedPayload(createdPayload.id)
          }
        )
      )
    )
}
