package training.graph

import cats.effect.Async
import cats.effect.std.Dispatcher
import sangria.schema._

import training.database.Manager
import training.model.Shop

object ShopType {

  def apply[F[_]: Async](dispatcher: Dispatcher[F]): ObjectType[Manager[F], Shop] =
    ObjectType(
      name        = "Shop",
      description = "Shop",
      fieldsFn = () =>
        fields(
          Field("id", IntType, Some("Unique identifier"), resolve                   = _.value.id),
          Field("name", StringType, Some("Name of the shop"), resolve               = _.value.name),
          Field("businessName", StringType, Some("Legal name of the shop"), resolve = _.value.name),
          Field(
            "activity",
            ActivityType[F],
            Some("Shop activity"),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.activities.findById(c.value.activityId))
          ),
          Field(
            "stratum",
            StratumType[F],
            Some("Shop stratum"),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.stratums.findById(c.value.stratumId))
          ),
          Field("address", StringType, Some("Shop legal address"), resolve    = _.value.address),
          Field("phoneNumber", StringType, Some("Shop phone number"), resolve = _.value.phoneNumber),
          Field("email", StringType, Some("Email"), resolve                   = _.value.email),
          Field("website", StringType, Some("Website"), resolve               = _.value.website),
          Field(
            "shopCategory",
            ShopCategoryType[F],
            Some("Shop category"),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.shopCategories.findById(c.value.shopCategoryId))
          ),
          Field("longitude", FloatType, Some("Name of the activity"), resolve = _.value.longitude),
          Field("latitude", FloatType, Some("Name of the activity"), resolve  = _.value.latitude)
        )
    )
}
