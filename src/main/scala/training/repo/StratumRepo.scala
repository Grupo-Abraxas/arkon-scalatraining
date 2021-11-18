package training.repo

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.Stratum

trait StratumRepo[F[_]] {
  def fetchAll: F[List[Stratum]]
  def fetchById(id: Option[String]): F[Option[Stratum]]
}

object StratumRepo {
  def fromTransactor[F[_]: Sync](xa: Transactor[F]): StratumRepo[F] =
    new StratumRepo[F] {
      val select: Fragment =
        fr"""
            SELECT id, name
            FROM stratum
          """

      def fetchAll: F[List[Stratum]] =
        select.query[Stratum].to[List].transact(xa)

      def fetchById(id: Option[String]): F[Option[Stratum]] =
        (select ++ fr"WHERE id = $id::INTEGER")
          .query[Stratum]
          .option
          .transact(xa)
    }
}
