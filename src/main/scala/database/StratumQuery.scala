package training.database

import cats.effect.Async
import doobie.Transactor
import doobie.implicits._

import training.model.Stratum

trait StratumQuery[F[_]] {
  def all: F[List[Stratum]]
  def findById(id: Int): F[Stratum]
  def exists(id: Int): F[Boolean]
}

object StratumQuery {
  val select = fr"select id, name from stratum"

  def fromTransactor[F[_]: Async](transactor: Transactor[F]): StratumQuery[F] =
    new StratumQuery[F] {

      def all: F[List[Stratum]] =
        select.query[Stratum].to[List].transact(transactor)

      def findById(id: Int): F[Stratum] =
        (select ++ fr"where id = $id").query[Stratum].unique.transact(transactor)

      def exists(id: Int): F[Boolean] = {
        val selectExists = sql"""
  				select exists(
						select id from stratum where id = $id
					)
				"""

        selectExists.query[Boolean].unique.transact(transactor)
      }
    }
}
