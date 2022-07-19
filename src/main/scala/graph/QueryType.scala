package training.graph

import cats.effect.Async
import cats.effect.std.Dispatcher
import sangria.schema._

import training.database.Manager

object QueryType {

  val Id        = Argument("id", IntType)
  val Limit     = Argument("limit", IntType, defaultValue = 50)
  val Offset    = Argument("offset", IntType, defaultValue = 0)
  val Radius    = Argument("radius", IntType, defaultValue = 50)
  val Longitude = Argument("longitude", FloatType)
  val Latitude  = Argument("latitude", FloatType)

  def apply[F[_]: Async](dispatcher: Dispatcher[F]): ObjectType[Manager[F], Unit] =
    ObjectType(
      name = "Query",
      fieldsFn = () =>
        fields(
          Field(
            "shop",
            OptionType(ShopType[F](dispatcher)),
            Some("Returns a shop with the given id, if it exists."),
						List(Id),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.shops.findById(c arg Id))
          ),
          Field(
            "shops",
            ListType(ShopType[F](dispatcher)),
            Some("Returns shops, if any exists."),
						List(Limit, Offset),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.shops.some(c arg Limit, c arg Offset))
          ),
          Field(
            "nearbyShops",
            ListType(ShopType[F](dispatcher)),
            Some("Returns nearby shops within the given coordinates, if any exists."),
						List(Longitude, Latitude, Limit),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.shops.filterByCoordinates(c arg Longitude, c arg Latitude, c arg Limit))
          ),
          Field(
            "shopsInRadius",
            ListType(ShopType[F](dispatcher)),
            Some("Returns shops within the given coordinates and radius, if any exists."),
						List(Longitude, Latitude, Radius),
            resolve = c => dispatcher.unsafeToFuture(c.ctx.shops.filterByRadius(c arg Longitude, c arg Latitude, c arg Radius))
          )
        )
    )
}
