package training.graph

import cats.effect.Async
import cats.effect.std.Dispatcher
import sangria.schema._
import sangria.macros.derive._

import training.database.Manager
import training.model.Shop

object ShopInput {

  def apply: InputObjectType[Shop] =
    deriveInputObjectType[Shop](
      InputObjectTypeName("ShopInput"),
      InputObjectTypeDescription("A new shop")
    )
}
