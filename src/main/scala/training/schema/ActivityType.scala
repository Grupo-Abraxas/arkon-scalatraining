package training.schema

import cats.effect.Effect
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}
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
            fieldType = IntType,
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
