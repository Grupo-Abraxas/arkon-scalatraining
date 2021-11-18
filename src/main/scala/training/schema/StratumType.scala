package training.schema

import cats.effect.Effect
import sangria.schema.{Field, IDType, ObjectType, StringType, fields}
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
