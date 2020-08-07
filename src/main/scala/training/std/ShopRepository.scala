package training.std

import cats.effect._
import doobie.implicits._
import training.std.Models._

class ShopRepository {
  val transactor = GlobalConnection().connect

  def shops(limit: Int = 50, offset: Int = 0) = {
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeRunSync.drop(offset).take(limit)
  }

  def shop(id: Int) = {
    val query: doobie.ConnectionIO[Shop] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop where id = $id
    """.query[Shop].unique
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def activities(ids: Seq[Int]) = {
    val query: doobie.ConnectionIO[List[ComercialActivity]] =
      sql"""
    select
      id, name
    from comercial_activity
    """.query[ComercialActivity].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def shopTypes(ids: Seq[Int]) = {
    val query: doobie.ConnectionIO[List[ShopType]] =
      sql"""
    select
      id, name
    from shop_type
    """.query[ShopType].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def stratums(ids: Seq[Int]) = {
    val query: doobie.ConnectionIO[List[Stratum]] =
      sql"""
    select
      id, name
    from Stratum
    """.query[Stratum].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def createShop(input: CreateShopInput) = {
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

  def activity(id: Int) = {
    val query: doobie.ConnectionIO[ComercialActivity] =
      sql"""
    select id, name
    from comercial_activity where id = $id
    """.query[ComercialActivity].unique
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def toCoordinates(idShop: Int) = {
    val query: doobie.ConnectionIO[Position] =
      sql"""
      SELECT ST_X(position::geometry) as lat, ST_Y(position::geometry) as long FROM shop where id = $idShop
    """.query[Position].unique
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def nearByShop(idShop: Int) = {
    val position: Position = toCoordinates(idShop)
    println(s"Position => $position")
    nearbyShops(lat = position.x, long = position.y)
  }

  def radiusByShop(idShop: Int) = {
    val position: Position = toCoordinates(idShop)
    println(s"Position => $position")
    shopsInRadius(lat = position.x, long = position.y)
  }

  def nearbyShops(limit: Int = 5, lat: Float = 0L, long: Float = 0L) = {
    println(s"nearbyShops => limit: $limit - lat: $lat - long: $long")
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    order by position <-> ST_MakePoint($lat, $long)
    limit $limit
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def shopsInRadius(radius: Int = 50, lat: Float, long: Float) = {
    println(s"shopsInRadius => radius: $radius - lat: $lat - long: $long")
    val query: doobie.ConnectionIO[List[Shop]] =
      sql"""
    select
      id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position
    from shop
    WHERE ST_DWithin(position, ST_MakePoint($lat, $long), $radius)
    """.query[Shop].to[List]
    transactor.use(query.transact[IO]).unsafeToFuture
  }

  def createShopTrx(shop: Shop) = {
    sql"""
    insert into shop
    (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
    values (${shop.id}, ${shop.name}, ${shop.business_name}, ${shop.activity_id}, ${shop.stratum_id},
    ${shop.address}, ${shop.phone_number}, ${shop.email}, ${shop.website}, ${shop.shop_type_id},
    ST_GeographyFromText(${shop.position}))
    """.update.run
  }
}
