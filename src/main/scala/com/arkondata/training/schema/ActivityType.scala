package com.arkondata.training.schema

import cats.effect.Effect
import com.arkondata.training.model.Activity
import com.arkondata.training.repo.MasterRepository
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}

object ActivityType {

  def apply[F[_]: Effect]: ObjectType[MasterRepository[F], Activity] =
    ObjectType(
      name     = "Activity",

      fieldsFn = () => fields(
        Field( "id", IntType, resolve = _.value.id ),
        Field( "name", StringType, resolve = _.value.name ),
      )

    )
}
