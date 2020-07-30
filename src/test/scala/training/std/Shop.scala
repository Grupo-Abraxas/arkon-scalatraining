package training.std

import cats.effect.IO
import doobie.implicits._
import training.std.ComercialActivity.transactor

case class Shop(id: Int,
                name: String,
                business_name: String,
                activity_id: Int,
                stratum_id: Int,
                address: String,
                phone_number: String,
                email: String,
                website: String,
                shop_type_id: Int,
                position: String) {}

object Shop {
  def apply(id: Int,
            name: String,
            business_name: String,
            activity_id: Int,
            stratum_id: Int,
            address: String,
            phone_number: String,
            email: String,
            website: String,
            shop_type_id: Int,
            position: String): Shop =
    new Shop(id: Int,
      name: String,
      business_name: String,
      activity_id: Int,
      stratum_id: Int,
      address: String,
      phone_number: String,
      email: String,
      website: String,
      shop_type_id: Int,
      position: String)

  def findAll() : List[Shop] = {
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def findById(id: Int) : Shop = {
    val query: doobie.ConnectionIO[Shop] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop where id = $id
    """.query[Shop].unique
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createShop(shop :Shop) : Shop = {
    val query : doobie.ConnectionIO[Shop] =
      sql"""
      insert into shop
      (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
      values (${shop.id}, ${shop.name}, ${shop.business_name}, ${shop.activity_id}, ${shop.stratum_id}, ${shop.address},
      ${shop.phone_number}, ${shop.email}, ${shop.website}, ${shop.shop_type_id},
      ST_GeographyFromText(${shop.position}))""".update
      .withUniqueGeneratedKeys("id", "name", "business_name", "activity_id", "stratum_id", "address",
      "phone_number", "email", "website", "shop_type_id", "position")
      transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createShopTrx(shop :Shop) : doobie.ConnectionIO[Int] = {
      sql"""
      insert into shop
      (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
      values (${shop.id}, ${shop.name}, ${shop.business_name}, ${shop.activity_id}, ${shop.stratum_id},
      ${shop.address}, ${shop.phone_number}, ${shop.email}, ${shop.website}, ${shop.shop_type_id},
      ST_GeographyFromText(${shop.position}))"""
      .update.run
  }

  def createAll(ca: ComercialActivity, st: ShopType, str: Stratum, shop: Shop) = {
    val rows = for {
      activity <- ComercialActivity.createComercialActivityTrx(ca)
      shopType <- ShopType.createShopTypeTrx(st)
      stratum <- Stratum.createStratumTrx(str)
      shop <- createShopTrx(shop)
    } yield activity + shopType + stratum + shop
    transactor.use(rows.transact[IO]).unsafeRunSync
  }
}
