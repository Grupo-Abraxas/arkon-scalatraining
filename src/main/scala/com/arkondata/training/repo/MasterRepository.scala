package com.arkondata.training.repo

import cats.effect._
import doobie._


final case class MasterRepository[ F[_] ](
       inegi: InegiRepo[ F ],
       stratumRepository: StratumRepository[ F ],
       shopTypeRepository: ShopTypeRepository[ F ],
       shopRepository: ShopRepository[ F ],
       activityRepository: ActivityRepository[ F ]
                                         )

object MasterRepository {

  def fromTransactor[ F[_]: Effect ](xa: Transactor[F]): MasterRepository[F] =
    MasterRepository(
      InegiRepo.fromTransactor( xa ),
      StratumRepository.fromTransactor( xa ),
      ShopTypeRepository.fromTransactor( xa ),
      ShopRepository.fromTransactor( xa ),
      ActivityRepository.fromTransactor( xa )
    )

}