package training.repo

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.Activity

trait ActivityRepo[F[_]] {
  def fetchAll: F[List[Activity]]
}

object ActivityRepo {
  def fromTransactor[F[_]: Sync](xa: Transactor[F]): ActivityRepo[F] =
    new ActivityRepo[F] {
      val select: Fragment =
        fr"""
            SELECT id, name
            FROM commercial_activity
          """

      def fetchAll: F[List[Activity]] =
        select.query[Activity].to[List].transact(xa)
    }
}
