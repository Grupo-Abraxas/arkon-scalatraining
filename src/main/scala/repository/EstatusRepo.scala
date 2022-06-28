package repository

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import model.Estatus

trait EstatusRepo {
  def findAllEstatus()
  def findEstatusById(id : Int)
}


object EstatusRepo {

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:mtbMX",
    "userApp",
    "userAppPs"
  )
  def findAllEstatus() : IO[List[String]] =
  {
    val query =sql"select description from c_estatus".query[String]
    val action = query.to[List]
    action.transact(xa)

  }

  def findEstatusById( id : Int) : IO[List[Estatus]] =
  {
    val query =sql"select id,  description from c_estatus where id = $id".query[Estatus]
    val action = query.to[List]
    action.transact(xa)
  }
}
