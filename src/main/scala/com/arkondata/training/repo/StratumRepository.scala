package com.arkondata.training.repo

import cats.effect.Effect.ops.toAllEffectOps
import cats.effect._
import com.arkondata.training.model.Stratum
import doobie.Transactor
import doobie.implicits._

trait StratumRepository[F[_]] {

  def getById(id: Int): F[  Stratum ]
  def getByName(name: String): F[ Option[ Stratum] ]
  def create(name: String): F[ Stratum ]
  def getOrCreate( name: String): F[ Stratum ]

}


object StratumRepository {


  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): StratumRepository[ F ] =

    new StratumRepository[ F ] {

      def getById(id: Int): F[  Stratum ]  = {
        val selectStratumById = sql""" select * from stratum where id = $id """
        selectStratumById.query[ Stratum ].unique.transact( xa )
      }

      def getByName(name: String) : F[ Option [ Stratum ] ] = {
        val selectStratum = sql""" select id, name from stratum where name=$name limit 1"""
        selectStratum.query[ Stratum ].option.transact( xa )
      }

      def create(name: String): F[ Stratum ] = {
        val insertStratumSql = sql""" insert into stratum (name) values ( $name ) returning id, name"""
        insertStratumSql.query[ Stratum ].unique.transact( xa )
      }

      def getOrCreate(name: String): F[ Stratum ] = {
        val stratumOption = getByName( name ).toIO.unsafeRunSync
        if ( stratumOption.isEmpty ) {
          create( name )
        } else {
          getById( stratumOption.get.id )
        }
      }

    }

}