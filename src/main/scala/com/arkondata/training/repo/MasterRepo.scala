package com.arkondata.training.repo

import cats.effect._
import doobie._
import io.chrisdavenport.log4cats.Logger

final case class MasterRepo[F[_]](
                                 inegi: InegiRepo
//                                   city:     CityRepo[F],
//                                   country:  CountryRepo[F],
//                                   language: LanguageRepo[F]
                                 )

object MasterRepo {
//
  def fromTransactor[F[_]: Sync: Logger](xa: Transactor[F]): MasterRepo[F] =
    MasterRepo(
        InegiRepo.fromTransactor
//
////      CityRepo.fromTransactor(xa),
////      CountryRepo.fromTransactor(xa),
////      LanguageRepo.fromTransactor(xa)
    )
//
}