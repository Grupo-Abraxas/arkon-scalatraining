package queries
import cats.effect.IO
import doobie.{FC, Transactor}
import doobie.implicits._
import models.Shop

class ShopQueries(xa: Transactor[IO]) {

  // consulta para crear una nueva tienda
  def create(shop: Shop): IO[Shop] = {
    val insertSql = sql"""
      INSERT INTO Shop (
        id, name, business_name, activity_id, stratum_id, address,
        phone_number, email, website, shop_type_id, location
      ) VALUES (
        ${shop.id}, ${shop.name}, ${shop.businessName}, ${shop.activityId},
        ${shop.stratumId}, ${shop.address}, ${shop.phoneNumber}, ${shop.email},
        ${shop.website}, ${shop.shopTypeId},
        ST_SetSRID(ST_MakePoint(${shop.long}, ${shop.lat}), 4326)
      )
    """.update.run

    insertSql.transact(xa).map(_ => shop)
  }

  def testConnection(): IO[Boolean] = {
    FC.isValid(1).transact(xa)
  }
}
