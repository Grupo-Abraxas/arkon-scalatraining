package training.graph

import cats.effect.Async
import sangria.schema._

import training.database.Manager
import training.model.Activity

object ActivityType {

  def apply[F[_]: Async]: ObjectType[Manager[F], Activity] =
    ObjectType(
      name        = "Activity",
      description = "Any activity related to economy",
      fieldsFn = () =>
        fields(
          Field("id", IntType, Some("Unique identifier"), resolve               = _.value.id),
          Field("name", StringType, Some("Definition of the activity"), resolve = _.value.name)
        )
    )
}
