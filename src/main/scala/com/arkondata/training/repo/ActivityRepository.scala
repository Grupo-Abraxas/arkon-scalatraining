package com.arkondata.training.repo


import cats.effect._
import cats.effect.implicits.toEffectOps
import com.arkondata.training.model.Activity
import doobie.Transactor
import doobie.implicits._

trait ActivityRepository[F[_]] {

    def getById(id: Int): F[  Activity ]
    def getByName(name: String): F[ Option[Activity] ]
    def create(name: String): F[ Activity ]
    def getOrCreate( name: String): F[ Activity ]
}


object ActivityRepository {


  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): ActivityRepository[ F ] =

    new ActivityRepository[ F ] {

      def getById(id: Int): F[  Activity ]  = {
        val selectActivityById = sql""" select * from comercial_activity where id = $id """
        selectActivityById.query[ Activity ].unique.transact( xa )
      }

      def getByName(name: String) : F[ Option [ Activity ] ] = {
        val selectActivity = sql""" select id, name from comercial_activity where name=$name limit 1"""
        selectActivity.query[ Activity ].option.transact( xa )
      }

      def create(name: String): F[ Activity ] = {
        val insertActivitySql = sql""" insert into comercial_activity (name) values ( $name ) returning id"""
        insertActivitySql.query[ Activity ].unique.transact( xa )
      }

      def getOrCreate(name: String): F[ Activity ] = {
        val activityOption = getByName( name ).toIO.unsafeRunSync
        if ( activityOption.isEmpty ) {
          create( name )
        } else {
          getById( activityOption.get.id )
        }
      }

    }

}