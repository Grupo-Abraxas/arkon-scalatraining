package com.arkondata.training.repo

import cats.effect._
import com.arkondata.training.model.Activity
import doobie.Transactor
import doobie.implicits._

trait ActivityRepository[F[_]] {

    def getById(id: Int): F[  Activity ]

}


object ActivityRepository {


  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): ActivityRepository[ F ] =

    new ActivityRepository[ F ] {

      def getById(id: Int): F[  Activity ]  = {
        val selectActivityById = sql""" select * from comercial_activity where id = $id """
        selectActivityById.query[ Activity ].unique.transact( xa )
      }


    }

}