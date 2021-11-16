package training.repo

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.ShopType

trait ShopTypeRepo[F[_]] {
  def fetchAll: F[List[ShopType]]
  def fetchById(id: String): F[Option[ShopType]]
}

object ShopTypeRepo {
  def fromTransactor[F[_]: Sync](xa: Transactor[F]): ShopTypeRepo[F] =
    new ShopTypeRepo[F] {
      val select: Fragment =
        fr"""
            SELECT id, name
            FROM shop_type
          """

      def fetchAll: F[List[ShopType]] =
        select.query[ShopType].to[List].transact(xa)

      def fetchById(id: String): F[Option[ShopType]] =
        (select ++ fr"WHERE id = $id::INTEGER")
          .query[ShopType]
          .option
          .transact(xa)
    }
}
