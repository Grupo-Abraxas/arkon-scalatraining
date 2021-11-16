package training.repo

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.Shop

trait ShopRepo[F[_]] {
  def fetchAll: F[List[Shop]]
  def fetchById(id: String): F[Option[Shop]]
  def fetchNearbyShopsByShopId(
      shopId: String,
      radius: Int = 50,
      limit: Int = 0
  ): F[List[Shop]]
}

object ShopRepo {
  def fromTransactor[F[_]: Sync](xa: Transactor[F]): ShopRepo[F] =
    new ShopRepo[F] {
      val select: Fragment =
        fr"""
          SELECT id,
            address, business_name, email,

            ST_Y(position::GEOMETRY) AS lat,
            ST_X(position::GEOMETRY) AS long,

            name, phone_number, website,

            activity_id, shop_type_id, stratum_id
          FROM shop
          """

      def fetchAll: F[List[Shop]] =
        select.query[Shop].to[List].transact(xa)

      def fetchById(id: String): F[Option[Shop]] =
        (select ++ fr"WHERE id = $id::INTEGER").query[Shop].option.transact(xa)

      def fetchNearbyShopsByShopId(
          shopId: String,
          radius: Int = 50,
          limit: Int = 0
      ): F[List[Shop]] = {
        val radiusCondition: Fragment = {
          fr"""
            ST_Distance(
              (SELECT position FROM shop WHERE id = $shopId::INTEGER LIMIT 1),
              position
            ) < $radius
            AND id <> $shopId::INTEGER
          """
        }

        val limitCondition: Fragment = limit match {
          case 0 => fr""
          case l => fr"LIMIT $l::INTEGER"
        }

        (select ++ fr"WHERE" ++ radiusCondition ++ limitCondition)
          .query[Shop]
          .to[List]
          .transact(xa)
      }
    }
}
