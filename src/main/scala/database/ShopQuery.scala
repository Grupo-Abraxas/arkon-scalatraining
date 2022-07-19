package training.database

import cats.effect.Async
import doobie.Transactor
import doobie.implicits._

import training.model.Shop

trait ShopQuery[F[_]] {
  def create(shop: Shop): F[Int]
  def all: F[List[Shop]]
  def some(limit: Int, offset: Int): F[List[Shop]]
  def filterByCoordinates(x: Double, y: Double, limit: Int): F[List[Shop]]
  def filterByRadius(x: Double, y: Double, radius: Int): F[List[Shop]]
  def findById(id: Int): F[Option[Shop]]
  def exists(id: Int): F[Boolean]
}

object ShopQuery {
  val select = fr"""
		select
		id, name, business_name, activity_id, stratum_id, address,
		phone_number, email, website, shop_category_id,
		ST_X(position) as longitude, ST_Y( position) as latitude
		from shop
	"""

  def fromTransactor[F[_]: Async](transactor: Transactor[F]): ShopQuery[F] =
    new ShopQuery[F] {

      def create(shop: Shop): F[Int] = {
        val insert = sql"""
  				insert into shop
					values(
						${shop.id}, ${shop.name}, ${shop.businessName}, ${shop.activityId},
						${shop.stratumId}, ${shop.address}, ${shop.phoneNumber}, ${shop.email},
						${shop.website}, ${shop.shopCategoryId}, Geography(ST_MakePoint(${shop.longitude}, ${shop.latitude}))
					)
				"""

        insert.update.withUniqueGeneratedKeys[Int]("id").transact(transactor)
      }

      def all: F[List[Shop]] =
        select.query[Shop].to[List].transact(transactor)

      def some(limit: Int, offset: Int): F[List[Shop]] =
        (select ++ fr"order by id" ++ fr"limit $limit" ++ fr"offset $offset")
          .query[Shop]
          .to[List]
          .transact(transactor)

      def filterByCoordinates(x: Double, y: Double, limit: Int): F[List[Shop]] =
        (select ++ fr"order by position <-> Geography(ST_MakePoint($x, $y))" ++ fr"limit $limit")
          .query[Shop]
          .to[List]
          .transact(transactor)

      def filterByRadius(x: Double, y: Double, radius: Int): F[List[Shop]] =
        (select ++ fr"where ST_DWithin(Geography(position), Geography(ST_MakePoint($x, $y)), $radius)")
          .query[Shop]
          .to[List]
          .transact(transactor)

      def findById(id: Int): F[Option[Shop]] =
        (select ++ fr"where id = $id").query[Shop].option.transact(transactor)

      def exists(id: Int): F[Boolean] = {
        val selectExists = sql"""
  				select exists(
						select id from shop where id = $id
					)
				"""

        selectExists.query[Boolean].unique.transact(transactor)
      }
    }
}
