package repository

import cats.effect.{ContextShift, IO}
import domain.Shop
import doobie.implicits._
import doobie.{ExecutionContexts, Read, Transactor}
import doobie.syntax.SqlInterpolator
import doobie.implicits._

import scala.concurrent.ExecutionContext


object Postgres {
  private implicit val cs = IO.contextShift(ExecutionContext.global)

  private val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://0.0.0.0:5432/inegishops",
    "postgres",
    "toor"
  )

  def getShops(): List[Shop] = {
    val res = sql"""SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id from shop"""
      .query[Shop]
      .to[List]
      .transact(transactor)
      .unsafeRunSync()
    print(res)
    res
  }

  def getShopById(id: Int): Shop = {
    Shop(
      id,
      "sdf",
      "sadf",
      0,
      0,
      "sdfas",
      "asdfas",
      "sdafasfd",
      "asdfsadf",
      0
    )
  }


}
