package com.arkondata.training.schema

import cats.effect.Effect
import com.arkondata.training.model.Stratum
import com.arkondata.training.repo.MasterRepository
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}

object StratumType {


  def apply[F[_]: Effect]: ObjectType[MasterRepository[F], Stratum] =
    ObjectType(
      name     = "Stratum",

      fieldsFn = () => fields(
        Field( "id", IntType, resolve = _.value.id ),
        Field( "name", StringType, resolve = _.value.name ),
      )

    )
}
