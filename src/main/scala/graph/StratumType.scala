package training.graph

import cats.effect.Async
import sangria.schema._

import training.database.Manager
import training.model.Stratum

object StratumType {

  def apply[F[_]: Async]: ObjectType[Manager[F], Stratum] =
    ObjectType(
      name        = "Stratum",
      description = "Stratum",
      fieldsFn = () =>
        fields(
          Field("id", IntType, Some("Unique identifier"), resolve              = _.value.id),
          Field("name", StringType, Some("Definition of the stratum"), resolve = _.value.name)
        )
    )
}
