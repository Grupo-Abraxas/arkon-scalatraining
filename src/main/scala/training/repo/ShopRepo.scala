package training.repo

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.{CreatedPayload, Shop}

trait ShopRepo[F[_]] {
  def create(data: Shop): F[CreatedPayload]
  def fetchAll(limit: Int, offset: Int): F[List[Shop]]
  def fetchById(id: String): F[Option[Shop]]
  def fetchNearbyShopsByShopId(
      shopId: String,
      radius: Int = 50,
      limit: Int = 0
  ): F[List[Shop]]
  def fetchShopsByPosition(
      lat: Double,
      long: Double,
      radius: Option[Int] = None,
      limit: Option[Int] = None
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

      def create(data: Shop): F[CreatedPayload] = sql"""
          INSERT INTO shop (
            id, activity_id, address, business_name, email, name,
            position, phone_number, shop_type_id, stratum_id
          )
          VALUES (
            ${data.id}::INTEGER, ${data.activity}::INTEGER, ${data.address}, ${data.businessName},
            ${data.email}, ${data.name},
            ST_SetSRID(ST_Point(${data.long}, ${data.lat}), 4326),
            ${data.phoneNumber}, ${data.shopType}::INTEGER, ${data.stratum}::INTEGER
          )
           """.update.withUniqueGeneratedKeys[CreatedPayload]("id").transact(xa)

      def fetchAll(limit: Int = 50, offset: Int = 0): F[List[Shop]] =
        (select ++ fr"LIMIT $limit::INTEGER OFFSET $offset::INTEGER")
          .query[Shop]
          .to[List]
          .transact(xa)

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

      def fetchShopsByPosition(
          lat: Double,
          long: Double,
          radius: Option[Int] = None,
          limit: Option[Int] = None
      ): F[List[Shop]] = {
        val radiusCondition: Fragment = radius match {
          case None => fr""
          case r =>
            fr"WHERE ST_Distance(ST_MakePoint($long, $lat), position) < $r"
        }

        val limitCondition: Fragment = (limit) match {
          case None => fr""
          case l    => fr"LIMIT $l::INTEGER"
        }

        (select ++ radiusCondition ++ limitCondition)
          .query[Shop]
          .to[List]
          .transact(xa)
      }
    }
}
