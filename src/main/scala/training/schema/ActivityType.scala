package training.schema

import cats.effect.Effect
import sangria.schema.{Field, IDType, ObjectType, StringType, fields}
import training.model.Activity
import training.repo.MasterRepo

object ActivityType {
  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Activity] =
    ObjectType(
      name = "Activity",
      fieldsFn = () =>
        fields(
          Field(
            name = "id",
            fieldType = IDType,
            resolve = _.value.id
          ),
          Field(
            name = "name",
            fieldType = StringType,
            resolve = _.value.name
          )
        )
    )
}
