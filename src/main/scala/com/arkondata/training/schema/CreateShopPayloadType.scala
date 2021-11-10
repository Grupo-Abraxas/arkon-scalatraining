package com.arkondata.training.schema

import cats.effect._
import com.arkondata.training.model.CreateShopPayload
import com.arkondata.training.repo.MasterRepository
import sangria.schema.{Field, IntType, ObjectType, fields}

object CreateShopPayloadType {

  def apply[F[_]: Effect]: ObjectType[ MasterRepository[F], CreateShopPayload ] =
    ObjectType(
      name     = "CreateShopPayload",
      fieldsFn = () => fields (
        Field( "id", IntType, resolve = _.value.id ),
      )

    )

}
