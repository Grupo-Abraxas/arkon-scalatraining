package training.graph

import cats.effect.Async
import cats.effect.std.Dispatcher
import io.circe.generic.auto._
import sangria.marshalling.circe._
import sangria.schema._

import training.database.Manager
import training.model.Shop

object MutationType {

  val CreateShopInput = Argument("input", ShopInput.apply)

  def apply[F[_]: Async](dispatcher: Dispatcher[F]): ObjectType[Manager[F], Unit] =
    ObjectType(
      name = "Mutation",
      fieldsFn = () =>
        fields(
          Field(
            "createShop",
            OptionType(IntType),
            Some(
              """
								Creates a new shop.

								If it already exists returns the id.

								If activity, stratum, shop category entries doesn't exists returns null.
							"""
            ),
            List(CreateShopInput),
            resolve = c => {
              def relationsExists(): Boolean =
                dispatcher.unsafeRunSync(c.ctx.activities.exists((c arg CreateShopInput).activityId)) &&
                dispatcher.unsafeRunSync(c.ctx.stratums.exists((c arg CreateShopInput).stratumId)) &&
                dispatcher.unsafeRunSync(c.ctx.shopCategories.exists((c arg CreateShopInput).shopCategoryId))

              val exists = dispatcher.unsafeRunSync(c.ctx.shops.exists((c arg CreateShopInput).id))

              if (exists)
                Some((c arg CreateShopInput).id)
              else if (relationsExists())
                Some(dispatcher.unsafeRunSync(c.ctx.shops.create(c arg CreateShopInput)))
              else
                None
            }
          )
        )
    )
}
