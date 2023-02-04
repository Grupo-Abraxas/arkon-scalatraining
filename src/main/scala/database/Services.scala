package database

import cats.effect._
import cats.effect.unsafe.implicits.global

import doobie.implicits._

import training.models.{Activity, Stratum, ShopType, Shop}

object Services {
    def findShopById(id: Long): IO[Option[Shop]] = Query.findShopById(id).transact(Database.tx)

    def getActivity(name: String): Activity = {
        val findActivity: Option[Activity] = findActivityByName(name).unsafeRunSync()
        findActivity getOrElse insertActivity(name).unsafeRunSync()
    }

    def getStratum(name: String): Stratum = {
        val findStratum: Option[Stratum] = findStratumByName(name).unsafeRunSync()
        findStratum getOrElse insertStratum(name).unsafeRunSync()
    }

    def getShopType(name: String): ShopType = {
        val findShopType: Option[ShopType] = findShopTypeByName(name).unsafeRunSync()
        findShopType getOrElse insertShopType(name).unsafeRunSync()
    }

    def insertShop(
        name: String,
        businessName: Option[String],
        activity: Activity,
        stratum: Stratum,
        roadType: String,
        street: String,
        extNum: String,
        intNum: String,
        settlement: String,
        postalCode: String,
        location: String,
        phoneNumber: Option[String],
        email: Option[String],
        website: Option[String],
        shopType: ShopType,
        longitude: String,
        latitude: String
    ): IO[Shop] =
        Query.insertShop(
            name,
            businessName,
            activity.id,
            stratum.id,
            s"$roadType $street $settlement $postalCode",
            phoneNumber,
            email,
            website,
            shopType.id,
            longitude,
            latitude
        ).transact(Database.tx)

    def findActivityByName(name: String): IO[Option[Activity]] =
        Query.findActivityByName(name).transact(Database.tx)

    def findStratumByName(name: String): IO[Option[Stratum]] =
        Query.findStratumByName(name).transact(Database.tx)

    def findShopTypeByName(name: String): IO[Option[ShopType]] =
        Query.findShopTypeByName(name).transact(Database.tx)

    def insertActivity(name: String): IO[Activity] =
        Query.insertActivity(name).transact(Database.tx)

    def insertStratum(name: String): IO[Stratum] =
        Query.insertStratum(name).transact(Database.tx)

    def insertShopType(name: String): IO[ShopType] =
        Query.insertShopType(name).transact(Database.tx)
}