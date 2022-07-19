package training.graph

import cats.effect.Async
import sangria.schema._

import training.database.Manager
import training.model.ShopCategory

object ShopCategoryType {

  def apply[F[_]: Async]: ObjectType[Manager[F], ShopCategory] =
    ObjectType(
      name        = "ShopCategory",
      description = "Shop category",
      fieldsFn = () =>
        fields(
          Field("id", IntType, Some("Unique identifier"), resolve                    = _.value.id),
          Field("name", StringType, Some("Definition of the shop category"), resolve = _.value.name)
        )
    )
}
