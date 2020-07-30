package training.std

import cats.effect._
import doobie.implicits._

case class ComercialActivity(id: Int, name: String) {}

object ComercialActivity {
  val transactor = GlobalConnection.transactor

  def apply(id: Int, name: String): ComercialActivity = new ComercialActivity(id, name)

  def findAll() : List[ComercialActivity] = {
    val query: doobie.ConnectionIO[List[ComercialActivity]] =
    sql"""
      select id, name from comercial_activity
    """.query[ComercialActivity].to[List]
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def findById(id: Int) : ComercialActivity = {
    val query: doobie.ConnectionIO[ComercialActivity] =
    sql"""
      select id, name from comercial_activity where id = $id
    """.query[ComercialActivity].unique
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createComercialActivity(ca :ComercialActivity) : ComercialActivity = {
    val query : doobie.ConnectionIO[ComercialActivity] =
    sql"""
      insert into comercial_activity (id, name) values (${ca.id}, ${ca.name})
    """.update.withUniqueGeneratedKeys("id", "name")
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createComercialActivityTrx(ca :ComercialActivity) : doobie.ConnectionIO[Int] = {
      sql"""
      insert into comercial_activity (id, name) values (${ca.id}, ${ca.name})
    """.update.run
  }
}