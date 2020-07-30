package training.std

import cats.effect.IO
import doobie.implicits._

case class Stratum(id: Int, name: String) {}

object Stratum {
  val transactor = GlobalConnection.transactor

  def apply(id: Int, name: String): Stratum = new Stratum(id, name)

  def findAll() : List[Stratum] = {
    val query: doobie.ConnectionIO[List[Stratum]] =
      sql"""
      select id, name from stratum
    """.query[Stratum].to[List]
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def findById(id: Int) : Stratum = {
    val query: doobie.ConnectionIO[Stratum] =
      sql"""
      select id, name from stratum where id = $id
    """.query[Stratum].unique
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createStratum(stratum :Stratum) : Stratum = {
    val query : doobie.ConnectionIO[Stratum] =
      sql"""
      insert into stratum (id, name) values (${stratum.id}, ${stratum.name})
    """.update.withUniqueGeneratedKeys("id", "name")
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createStratumTrx(stratum :Stratum) : doobie.ConnectionIO[Int] = {
      sql"""
      insert into stratum (id, name) values (${stratum.id}, ${stratum.name})
    """.update.run
  }
}
