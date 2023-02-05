package database

import cats.effect._
import cats.effect.unsafe.implicits.global

import doobie.implicits._

import training.models.{Activity, Stratum, ShopType, Shop, ShopInput}

object Services {
    def findShopById(id: Long): IO[Option[Shop]] =
        Query.findShopById(id).transact(Database.tx)

    def listShops(limit: Int, offset: Int): IO[List[Shop]] =
        Query.listShops(limit, offset).transact(Database.tx)

    def nearbyShops(limit: Int, lat: Double, lng: Double): IO[List[Shop]] =
        Query.nearbyShops(limit, lat, lng).transact(Database.tx)

    def shopsInRadius(radius: Int, lat: Double, lng: Double): IO[List[Shop]] =
        Query.shopsInRadius(radius, lat, lng).transact(Database.tx)

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

    def insertShop(shopInput: ShopInput
    ): IO[Shop] = {
        val activity: Activity = Services.getActivity(shopInput.activity)
        val stratum: Stratum = Services.getStratum(shopInput.stratum)
        val shopType: ShopType = Services.getShopType(shopInput.shopType)

        Query.insertShop(
            shopInput.name,
            shopInput.businessName,
            activity.id,
            stratum.id,
            shopInput.address,
            shopInput.phoneNumber,
            shopInput.email,
            shopInput.website,
            shopType.id,
            shopInput.latitude,
            shopInput.longitude
        ).transact(Database.tx)
    }

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