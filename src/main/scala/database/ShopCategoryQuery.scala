package training.database

import cats.effect.Async
import doobie.Transactor
import doobie.implicits._

import training.model.ShopCategory

trait ShopCategoryQuery[F[_]] {
  def all: F[List[ShopCategory]]
  def findById(id: Int): F[ShopCategory]
  def exists(id: Int): F[Boolean]
}

object ShopCategoryQuery {
  val select = fr"select id, name from shop_category"

  def fromTransactor[F[_]: Async](transactor: Transactor[F]): ShopCategoryQuery[F] =
    new ShopCategoryQuery[F] {

      def all: F[List[ShopCategory]] =
        select.query[ShopCategory].to[List].transact(transactor)

      def findById(id: Int): F[ShopCategory] =
        (select ++ fr"where id = $id").query[ShopCategory].unique.transact(transactor)

      def exists(id: Int): F[Boolean] = {
        val selectExists = sql"""
  				select exists(
						select id from shop_category where id = $id
					)
				"""

        selectExists.query[Boolean].unique.transact(transactor)
      }
    }
}
