package training.services

import training.models._
import doobie._
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.postgres.implicits._

object ShopQueries {

  val logHandler: LogHandler = LogHandler.jdkLogHandler

  def getShop(id: Int): ConnectionIO[Option[Shop]] = {
    sql"""
      SELECT id, name, business_name, activity_id, stratum_id, address,
      phone_number, email, website, shop_type_id, position 
      FROM shop WHERE id = $id
    """.queryWithLogHandler[Shop](logHandler)
      .option
  }

  def getShops(limit: Int, offset: Int): ConnectionIO[List[Shop]] = {
    sql"""
      SELECT id, name, business_name, activity_id, stratum_id,
      address, phone_number, email, website, shop_type_id, position 
      FROM shop LIMIT $limit OFFSET $offset
    """.queryWithLogHandler[Shop](logHandler)
      .to[List]
  }

  def getNearbyShops(lat: Float, long: Float, radius: Int, limit: Int): ConnectionIO[List[Shop]] = {
    sql"""
      SELECT id, name, business_name, activity_id, stratum_id,
      address, phone_number, email, website, shop_type_id, position
      FROM shop
      WHERE ST_DistanceSphere(position, ST_MakePoint($long, $lat)) < $radius
      LIMIT $limit
    """.queryWithLogHandler[Shop](logHandler)
      .to[List]
  }

  def createShop(shop: Shop): ConnectionIO[Int] = {
    sql"""
      INSERT INTO shop (
        name, business_name, activity_id, stratum_id,
        address, phone_number, email, website, shop_type_id, position
      ) 
      VALUES (
        ${shop.name},
        ${shop.businessName},
        ${shop.activityId},
        ${shop.stratumId},
        ${shop.address},
        ${shop.phoneNumber},
        ${shop.email},
        ${shop.website},
        ${shop.shopTypeId},
        ${shop.position}
      )
    """.updateWithLogHandler(logHandler)
      .run
  }
}