// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package training.schema

import cats.effect.Effect
import cats.effect.implicits._
import sangria.schema._
import training.repo.MasterRepo

object QueryType {
  val IdArg: Argument[String] = Argument(
    name = "id",
    argumentType = IDType
  )

  val LatArg: Argument[Double] = Argument(
    name = "lat",
    argumentType = FloatType
  )

  val LongArg: Argument[Double] = Argument(
    name = "long",
    argumentType = FloatType
  )

  val LimitArg5: Argument[Int] = Argument(
    name = "limit",
    argumentType = OptionInputType(IntType),
    defaultValue = 5
  )

  val LimitArg50: Argument[Int] = Argument(
    name = "limit",
    argumentType = OptionInputType(IntType),
    defaultValue = 50
  )

  val OffsetArg: Argument[Int] = Argument(
    name = "offset",
    argumentType = OptionInputType(IntType),
    defaultValue = 0
  )

  val RadiusArg: Argument[Int] = Argument(
    name = "radius",
    argumentType = OptionInputType(IntType),
    defaultValue = 50
  )

  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Unit] =
    ObjectType(
      name = "Query",
      fields = fields(
        // Field(
        //   name = "activities",
        //   fieldType = ListType(ActivityType[F]),
        //   resolve = c => c.ctx.activity.fetchAll.toIO.unsafeToFuture
        // ),
        Field(
          name = "nearbyShops",
          fieldType = ListType(ShopType[F]),
          arguments = List(LimitArg5, LatArg, LongArg),
          resolve = c =>
            c.ctx.shop
              .fetchShopsByPosition(
                c.arg(LatArg),
                c.arg(LongArg),
                limit = c.argOpt(LimitArg5)
              )
              .toIO
              .unsafeToFuture
        ),
        Field(
          name = "shop",
          fieldType = OptionType(ShopType[F]),
          arguments = List(IdArg),
          resolve = c => c.ctx.shop.fetchById(c.arg(IdArg)).toIO.unsafeToFuture
        ),
        Field(
          name = "shops",
          fieldType = ListType(ShopType[F]),
          arguments = List(LimitArg50, OffsetArg),
          resolve = c =>
            c.ctx.shop
              .fetchAll(c.arg(LimitArg50), c.arg(OffsetArg))
              .toIO
              .unsafeToFuture
        ),
        Field(
          name = "shopsInRadius",
          fieldType = ListType(ShopType[F]),
          arguments = List(RadiusArg, LatArg, LongArg),
          resolve = c =>
            c.ctx.shop
              .fetchShopsByPosition(
                c.arg(LatArg),
                c.arg(LongArg),
                radius = c.argOpt(RadiusArg)
              )
              .toIO
              .unsafeToFuture
        ),
        // Field(
        //   name = "shopTypes",
        //   fieldType = ListType(ShopTypeType[F]),
        //   resolve = c => c.ctx.shopType.fetchAll.toIO.unsafeToFuture
        // )
        // Field(
        //  name = "stratums",
        //  fieldType = ListType(StratumType[F]),
        //  resolve = c => c.ctx.stratum.fetchAll.toIO.unsafeToFuture
        // )
      )
    )

  def schema[F[_]: Effect]: Schema[MasterRepo[F], Unit] =
    Schema(QueryType[F])
}
