// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package training.repo

import cats.effect._
import doobie._

final case class MasterRepo[F[_]](
    activity: ActivityRepo[F],
    shop: ShopRepo[F],
    shopType: ShopTypeRepo[F],
    stratum: StratumRepo[F]
)

object MasterRepo {
  def fromTransactor[F[_]: Sync](xa: Transactor[F]): MasterRepo[F] =
    MasterRepo(
      ActivityRepo.fromTransactor(xa),
      ShopRepo.fromTransactor(xa),
      ShopTypeRepo.fromTransactor(xa),
      StratumRepo.fromTransactor(xa)
    )
}
