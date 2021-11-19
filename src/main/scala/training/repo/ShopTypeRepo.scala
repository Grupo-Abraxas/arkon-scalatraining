package training.repo

import cats.data.OptionT
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.ShopType

trait ShopTypeRepo[F[_]] {
  def fetchAll: F[List[ShopType]]
  def fetchById(id: Option[String]): F[Option[ShopType]]
  def fetchOrCreateByName(name: Option[String]): F[ShopType]
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

      def fetchById(id: Option[String]): F[Option[ShopType]] =
        (select ++ fr"WHERE id = $id::INTEGER")
          .query[ShopType]
          .option
          .transact(xa)

      def fetchOrCreateByName(name: Option[String]): F[ShopType] =
        OptionT(
          (select ++ fr"WHERE name = $name").query[ShopType].option
        ).getOrElseF(
          sql"""
               INSERT INTO shop_type(id, name) 
               VALUES((SELECT MAX(id) + 1 FROM shop_type), $name)
          """.update
            .withUniqueGeneratedKeys[ShopType]("id", "name")
        ).transact(xa)
    }
}
