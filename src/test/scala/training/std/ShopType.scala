package training.std

import cats.effect.IO
import doobie.implicits._

case class ShopType(id: Int, name: String) {}

object ShopType {
  val transactor = GlobalConnection.transactor

  def apply(id: Int, name: String): ShopType = new ShopType(id, name)

  def findAll() : List[ShopType] = {
    val query: doobie.ConnectionIO[List[ShopType]] =
      sql"""
      select id, name from shop_type
    """.query[ShopType].to[List]
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def findById(id: Int) : ShopType = {
    val query: doobie.ConnectionIO[ShopType] =
      sql"""
      select id, name from shop_type where id = $id
    """.query[ShopType].unique
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createShopType(shop :ShopType) : ShopType = {
    val query : doobie.ConnectionIO[ShopType] =
      sql"""
      insert into shop_type (id, name) values (${shop.id}, ${shop.name})
    """.update.withUniqueGeneratedKeys("id", "name")
    transactor.use(query.transact[IO]).unsafeRunSync
  }

  def createShopTypeTrx(shop :ShopType) : doobie.ConnectionIO[Int] = {
      sql"""
      insert into shop_type (id, name) values (${shop.id}, ${shop.name})
    """.update.run
  }
}
