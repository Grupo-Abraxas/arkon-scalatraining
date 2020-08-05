package training.std

import cats.effect._
import doobie.implicits._
import training.std.Models._

import scala.concurrent.Future

class ShopRepository {
  val transactor = GlobalConnection.transactor

  def shops(limit: Int = 50, offset: Int = 0): List[Shop] = {
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeRunSync.drop(offset).take(limit)
  }

  def shop(id: Int): Future[Shop] = {
    val query: doobie.ConnectionIO[Shop] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop where id = $id
    """.query[Shop].unique
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def activities(ids: Seq[Int]): Future[Seq[ComercialActivity]] = {
    val query: doobie.ConnectionIO[List[ComercialActivity]] =
      sql"""
    select
      id, name
    from comercial_activity
    """.query[ComercialActivity].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def shopTypes(ids: Seq[Int]): Future[Seq[ShopType]] = {
    val query: doobie.ConnectionIO[List[ShopType]] =
      sql"""
    select
      id, name
    from shop_type
    """.query[ShopType].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def stratums(ids: Seq[Int]): Future[Seq[Stratum]] = {
    val query: doobie.ConnectionIO[List[Stratum]] =
      sql"""
    select
      id, name
    from Stratum
    """.query[Stratum].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def createShop(input: CreateShopInput): Future[CreateShopPayload] = {
    val query: doobie.ConnectionIO[CreateShopPayload] =
      sql"""
      insert into shop
      (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
      values (
      ${input.id}, ${input.name}, ${input.businessName}, ${input.activity.toInt}, ${input.stratum.toInt},
      ${input.address}, ${input.phoneNumber}, ${input.email}, ${input.website}, ${input.shopType.toInt},
      ST_SetSRID(ST_Point( ${input.long}, ${input.lat} ), 4326)::geography)
      """.update
        .withUniqueGeneratedKeys("id", "name", "business_name", "activity_id", "stratum_id", "address",
          "phone_number", "email", "website", "shop_type_id", "position")
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def activity(id: Int): Future[ComercialActivity] = {
    val query: doobie.ConnectionIO[ComercialActivity] =
      sql"""
    select id, name
    from comercial_activity where id = $id
    """.query[ComercialActivity].unique
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def toCoordinates(idShop: Int): Position = {
    val query: doobie.ConnectionIO[Position] =
      sql"""
      SELECT ST_X(position::geometry) as lat, ST_Y(position::geometry) as long FROM shop where id = $idShop
    """.query[Position].unique
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def nearByShop(idShop: Int): Future[List[Shop]] = {
    val position: Position = toCoordinates(idShop)
    nearbyShops(lat = position.x, long = position.y)
  }

  def nearbyShops(limit: Int = 5, lat: Float = 0L, long: Float = 0L): Future[List[Shop]] = {
    println(s"nearbyShops => limit: $limit - lat: $lat - long: $long")
    val point = "POINT(" + lat + " " + long + ")"
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    order by position <-> ST_GeogFromText($point)
    limit $limit
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def radiusByShop(idShop: Int): Future[List[Shop]] = {
    val position: Position = toCoordinates(idShop)
    shopsInRadius(lat = position.x, long = position.y)
  }

  def shopsInRadius(radius: Int = 50, lat: Float, long: Float): Future[List[Shop]] = {
    println(s"shopsInRadius => limit: $radius - lat: $lat - long: $long")
    val point = "ST_MakePoint(" + lat + "," + long + ")::geography"
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    WHERE ST_DWithin(position, ST_MakePoint($lat,$long), $radius)
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def createShopTrx(shop: Shop): doobie.ConnectionIO[Int] = {
    sql"""
    insert into shop
    (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
    values (${shop.id}, ${shop.name}, ${shop.business_name}, ${shop.activity_id}, ${shop.stratum_id},
    ${shop.address}, ${shop.phone_number}, ${shop.email}, ${shop.website}, ${shop.shop_type_id},
    ST_GeographyFromText(${shop.position}))
    """.update.run
  }

  def createComercialActivityTrx(ca: ComercialActivity): doobie.ConnectionIO[Int] = {
    sql"""
      insert into comercial_activity (id, name) values (${ca.id}, ${ca.name})
    """.update.run
  }

  def createShopTypeTrx(shop: ShopType): doobie.ConnectionIO[Int] = {
    sql"""
      insert into shop_type (id, name) values (${
      shop.id
    }, ${
      shop.name
    })
    """.update.run
  }

  def createStratumTrx(stratum: Stratum): doobie.ConnectionIO[Int] = {
    sql"""
      insert into stratum (id, name) values (${
      stratum.id
    }, ${
      stratum.name
    })
    """.update.run
  }

  def createAll(ca: ComercialActivity, st: ShopType, str: Stratum, shop: Shop) = {
    val rows = for {
      activity <- createComercialActivityTrx(ca)
      shopType <- createShopTypeTrx(st)
      stratum <- createStratumTrx(str)
      shop <- createShopTrx(shop)
    } yield activity + shopType + stratum + shop
    transactor.use(rows.transact[IO]).unsafeToFuture
  }
}

