package training.repo

import cats.data.OptionT
import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import training.model.Activity

trait ActivityRepo[F[_]] {
  def fetchAll: F[List[Activity]]
  def fetchById(id: Option[String]): F[Option[Activity]]
  def fetchOrCreateByName(name: Option[String]): F[Activity]
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

      def fetchById(id: Option[String]): F[Option[Activity]] =
        (select ++ fr"WHERE id = $id::INTEGER")
          .query[Activity]
          .option
          .transact(xa)

      def fetchOrCreateByName(name: Option[String]): F[Activity] =
        OptionT(
          (select ++ fr"WHERE name = $name").query[Activity].option
        ).getOrElseF(
          sql"""
               INSERT INTO commercial_activity(id, name) 
               VALUES((SELECT MAX(id) + 1 FROM commercial_activity), $name)
          """.update
            .withUniqueGeneratedKeys[Activity]("id", "name")
        ).transact(xa)
    }
}
