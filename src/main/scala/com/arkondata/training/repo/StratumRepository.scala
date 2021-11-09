package com.arkondata.training.repo

import cats.effect._
import com.arkondata.training.model.Stratum
import doobie.Transactor
import doobie.implicits._

trait StratumRepository[F[_]] {

  def getById(id: Int): F[  Stratum ]

}


object StratumRepository {


  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): StratumRepository[ F ] =

    new StratumRepository[ F ] {

      def getById(id: Int): F[  Stratum ]  = {
        val selectStratumById = sql""" select * from stratum where id = $id """
        selectStratumById.query[ Stratum ].unique.transact( xa )
      }

    }

}