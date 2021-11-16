package training.schema

import cats.effect.Effect
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}
import training.model.ShopType
import training.repo.MasterRepo

object ShopTypeType {
  def apply[F[_]: Effect]: ObjectType[MasterRepo[F], ShopType] =
    ObjectType(
      name = "ShopType",
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
