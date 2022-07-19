package training.database

import cats.effect.Async
import doobie.Transactor
import doobie.implicits._

import training.model.Activity

trait ActivityQuery[F[_]] {
  def all: F[List[Activity]]
  def findById(id: Int): F[Activity]
  def exists(id: Int): F[Boolean]
}

object ActivityQuery {
  val select = fr"select id, name from activity"

  def fromTransactor[F[_]: Async](transactor: Transactor[F]): ActivityQuery[F] =
    new ActivityQuery[F] {

      def all: F[List[Activity]] =
        select.query[Activity].to[List].transact(transactor)

      def findById(id: Int): F[Activity] =
        (select ++ fr"where id = $id").query[Activity].unique.transact(transactor)

      def exists(id: Int): F[Boolean] = {
        val selectExists = sql"""
  				select exists(
						select id from activity where id = $id
					)
				"""

        selectExists.query[Boolean].unique.transact(transactor)
      }
    }
}
