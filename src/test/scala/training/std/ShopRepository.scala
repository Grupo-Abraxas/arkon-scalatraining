package training.std

import slick.jdbc.PostgresProfile.api._
import training.std.Models._
import training.std.ShopRepository._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class ShopRepository(db: Database) {
  def addShop(id: Int,
                  name: String,
                  business_name: String,
                  activity_id: Int,
                  stratum_id: Int,
                  address: String,
                  phone_number: String,
                  email: String,
                  website: String,
                  shop_type_id: Int,
                  position: String): Future[Shop] = {
    val shop: Shop =
      Shop(id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
    db.run(ShopTQ.insertOrUpdate(shop)).map(_ => shop)
  }

  def shop(id: Int) =
    db.run(ShopTQ.filter(_.shopId === id).result.headOption)

  def shops(ids: Seq[Int]): Future[Seq[Shop]] =
    db.run(ShopTQ.filter(_.shopId inSet ids).result)

  def activities(ids: Seq[Int]): Future[Seq[ComercialActivity]] =
    db.run(CActivityTQ.filter(_.activityId inSet ids).result)

  def activityByRelShopIds(ids: Seq[Int]): Future[Seq[ComercialActivity]] = {
    db.run { CActivityTQ.filter(_.activityId inSet ids).result }
  }

  def shopTypes(ids: Seq[Int]): Future[Seq[ShopType]] =
    db.run(ShopTypeTQ.filter(_.shopTypeId inSet ids).result)

  def shopTypeByRelShopIds(ids: Seq[Int]): Future[Seq[ShopType]] = {
    db.run { ShopTypeTQ.filter(_.shopTypeId inSet ids).result }
  }

  def stratums(ids: Seq[Int]): Future[Seq[Stratum]] =
    db.run(StratumTQ.filter(_.stratumId inSet ids).result)

  def stratumsByRelShopIds(ids: Seq[Int]): Future[Seq[Stratum]] = {
    db.run { StratumTQ.filter(_.stratumId inSet ids).result }
  }

}

//Table mapping
object ShopRepository {
  class ShopTable(tag: Tag) extends Table[Shop](tag, "shop") {
    def shopId = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def business = column[String]("business_name")

    def activityId = column[Int]("activity_id")

    def stratumId = column[Int]("stratum_id")

    def address = column[String]("address")

    def phone = column[String]("phone_number")

    def email = column[String]("email")

    def website = column[String]("website")

    def shopTypeID = column[Int]("shop_type_id")

    def position = column[String]("position")

    def activityFK =
      foreignKey("activity_id", activityId, CActivityTQ)(_.activityId)
    def shopTypeFK =
      foreignKey("shop_type_id", shopTypeID, ShopTypeTQ)(_.shopTypeId)
    def stratumFK =
      foreignKey("stratum_id", stratumId, StratumTQ)(_.stratumId)

    def * =
      (shopId, name, business, activityId, stratumId, address, phone, email, website, shopTypeID, position) <>
      ((Shop.apply _).tupled, Shop.unapply)
  }

  class ComercialActivityTable(tag: Tag) extends Table[ComercialActivity](tag, "comercial_activity") {
    def activityId = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def * = (activityId, name) <>
      ((ComercialActivity.apply _).tupled, ComercialActivity.unapply)
  }

  class ShopTypeTable(tag: Tag) extends Table[ShopType](tag, "shop_type") {
    def shopTypeId = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def * = (shopTypeId, name) <>
      ((ShopType.apply _).tupled, ShopType.unapply)
  }

  class StratumTable(tag: Tag) extends Table[Stratum](tag, "stratum") {
    def stratumId = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def * = (stratumId, name) <>
      ((Stratum.apply _).tupled, Stratum.unapply)
  }

  val CActivityTQ = TableQuery[ComercialActivityTable]
  val ShopTQ = TableQuery[ShopTable]
  val ShopTypeTQ = TableQuery[ShopTypeTable]
  val StratumTQ = TableQuery[StratumTable]

  def createDatabase() = {
    val db = Database.forConfig("inegi")
    new ShopRepository(db)
  }

}
