package repository

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import doobie.implicits._
import doobie.util.transactor.Transactor
import model.{Estado}

trait EstadoRepo {
  def findAllEstado(): List[Estado]
  def findEstadoById(id : Int) :Estado
}


object EstadoRepo {

  implicit class Debugger[A](io:IO[A]){
    def debug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }
  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:mtbMX",
    "userApp",
    "userAppPs"
  )
  def findAllEstado() : List[Estado] =
  {
    val query =sql"select id, description from c_estatus".query[Estado]
    val action = query.stream.compile.toList
    action.transact(xa).unsafeRunSync()
  }

  def findEstadoById( id : Int) : Estado =
  {
    val query =sql"select id,  description from c_estatus where id = $id".query[Estado]
    val action = query.unique
    action.transact(xa).unsafeRunSync()
  }
}
