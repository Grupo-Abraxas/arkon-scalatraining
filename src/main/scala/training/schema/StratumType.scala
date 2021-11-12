package training.schema

import cats.effect.Effect
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}
import training.model.Stratum
import training.repo.MasterRepo

object StratumType {
  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], Stratum] =
    ObjectType(
      name = "Stratum",
      fieldsFn = () =>
        fields(
          Field(
            name = "id",
            fieldType = IntType,
            description = Some("Stratum id."),
            resolve = _.value.id
          ),
          Field(
            name = "name",
            fieldType = StringType,
            description = Some("Stratum name."),
            resolve = _.value.name
          )
        )
    )
}
