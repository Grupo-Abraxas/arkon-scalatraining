package training.services

import training.models._
import scala.concurrent.{ExecutionContext, Future}
import org.postgresql.geometric.PGpoint
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.log.LogHandler
import doobie.postgres.implicits._
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._

class DataService(transactor: Transactor[IO])(implicit ec: ExecutionContext) {

  val doobieLogHandler: LogHandler = LogHandler.jdkLogHandler

  def getShop(id: Int): Future[Option[Shop]] = {
    sql"SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position FROM shops WHERE id = $id"
      .queryWithLogHandler[Shop](doobieLogHandler)
      .option
      .transact(transactor)
      .unsafeToFuture()
  }

  def getShops(limit: Int, offset: Int): Future[Seq[Shop]] = {
    sql"SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position FROM shops LIMIT $limit OFFSET $offset"
      .queryWithLogHandler[Shop](doobieLogHandler)
      .to[Seq]
      .transact(transactor)
      .unsafeToFuture()
  }

  def getNearbyShops(lat: Float, long: Float, limit: Int): Future[Seq[Shop]] = {
    sql"""
      SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
      FROM shops
      WHERE ST_DistanceSphere(position, ST_MakePoint($long, $lat)) < $limit
      LIMIT $limit
    """.queryWithLogHandler[Shop](doobieLogHandler)
      .to[Seq]
      .transact(transactor)
      .unsafeToFuture()
  }

  def createShop(shop: Shop): Future[Int] = {
    sql"INSERT INTO shops (name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position) VALUES (${shop.name}, ${shop.businessName}, ${shop.activityId}, ${shop.stratumId}, ${shop.address}, ${shop.phoneNumber}, ${shop.email}, ${shop.website}, ${shop.shopTypeId}, ${shop.position})"
      .updateWithLogHandler(doobieLogHandler)
      .run
      .transact(transactor)
      .unsafeToFuture()
  }
}
