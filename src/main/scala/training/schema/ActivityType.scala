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
            description = Some("Activity id."),
            resolve = _.value.id
          ),
          Field(
            name = "name",
            fieldType = StringType,
            description = Some("Activity name."),
            resolve = _.value.name
          )
        )
    )
}
