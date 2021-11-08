package com.arkondata.training.repo

import cats.effect._
import doobie._


final case class MasterRepo[ F[_] ]( inegi: InegiRepo[ F ] )

object MasterRepo {

  def fromTransactor[ F[_]: Effect ](xa: Transactor[F]): MasterRepo[F] =
    MasterRepo( InegiRepo.fromTransactor( xa ) )

}