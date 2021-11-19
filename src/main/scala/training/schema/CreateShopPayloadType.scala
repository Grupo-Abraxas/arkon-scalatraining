package training.schema

import cats.effect.Effect
import sangria.schema.{Field, IDType, ObjectType, fields}
import training.model.CreatedPayload

object CreateShopPayloadType {
  def apply[F[_]: Effect]: ObjectType[Unit, CreatedPayload] =
    ObjectType(
      name = "CreateShopPayload",
      fields = fields(
        Field(
          name = "id",
          fieldType = IDType,
          resolve = _.value.id
        )
      )
    )
}
