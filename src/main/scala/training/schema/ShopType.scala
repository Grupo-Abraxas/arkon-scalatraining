package training.schema

import cats.effect.Effect
import cats.effect.implicits._
import sangria.schema._
import training.model.Shop
import training.repo.MasterRepo

object ShopType {
  val LimitArg: Argument[Int] = Argument(
    name = "limit",
    argumentType = IntType,
    defaultValue = 5
  )

  val RadiusArg: Argument[Int] = Argument(
    name = "radius",
    argumentType = IntType,
    defaultValue = 50
  )

  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Shop] =
    ObjectType(
      name = "Shop",
      fieldsFn = () =>
        fields(
          Field(
            name = "id",
            fieldType = IDType,
            resolve = _.value.id
          ),
          Field(
            name = "activity",
            fieldType = OptionType(ActivityType[F]),
            resolve = c =>
              c.ctx.activity.fetchById(c.value.activityId).toIO.unsafeToFuture
          ),
          Field(
            name = "address",
            fieldType = StringType,
            resolve = _.value.address
          ),
          Field(
            name = "businessName",
            fieldType = OptionType(StringType),
            resolve = _.value.businessName
          ),
          Field(
            name = "email",
            fieldType = OptionType(StringType),
            resolve = _.value.email
          ),
          Field(
            name = "lat",
            fieldType = FloatType,
            resolve = _.value.lat
          ),
          Field(
            name = "long",
            fieldType = FloatType,
            resolve = _.value.long
          ),
          Field(
            name = "name",
            fieldType = StringType,
            resolve = _.value.name
          ),
          Field(
            name = "nearbyShops",
            fieldType = ListType(ShopType[F]),
            arguments = List(LimitArg),
            resolve = c =>
              c.ctx.shop
                .fetchNearbyShopsByShopId(c.value.id, limit = c.arg(LimitArg))
                .toIO
                .unsafeToFuture
          ),
          Field(
            name = "phoneNumber",
            fieldType = OptionType(StringType),
            resolve = _.value.phoneNumber
          ),
          Field(
            name = "shopsInRadius",
            fieldType = ListType(ShopType[F]),
            arguments = List(RadiusArg),
            resolve = c =>
              c.ctx.shop
                .fetchNearbyShopsByShopId(c.value.id, c.arg(RadiusArg))
                .toIO
                .unsafeToFuture
          ),
          Field(
            name = "shopType",
            fieldType = OptionType(ShopTypeType[F]),
            resolve = c =>
              c.ctx.shopType.fetchById(c.value.shopTypeId).toIO.unsafeToFuture
          ),
          Field(
            name = "stratum",
            fieldType = OptionType(StratumType[F]),
            resolve = c =>
              c.ctx.stratum.fetchById(c.value.stratumId).toIO.unsafeToFuture
          ),
          Field(
            name = "website",
            fieldType = OptionType(StringType),
            resolve = _.value.website
          )
        )
    )
}
