package database

import cats.effect._
import cats.effect.unsafe.implicits.global

import doobie.implicits._
import doobie.hikari.HikariTransactor

import training.models.{Activity, Stratum, ShopType, Shop, ShopInput}

class Services(db: HikariTransactor[IO]) {
    def findShopById(id: Long): IO[Option[Shop]] =
        Query.findShopById(id).transact(db)

    def listShops(limit: Int, offset: Int): IO[List[Shop]] =
        Query.listShops(limit, offset).transact(db)

    def nearbyShops(limit: Int, lat: Double, lng: Double): IO[List[Shop]] =
        Query.nearbyShops(limit, lat, lng).transact(db)

    def shopsInRadius(radius: Int, lat: Double, lng: Double): IO[List[Shop]] =
        Query.shopsInRadius(radius, lat, lng).transact(db)

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
        val activity: Activity = getActivity(shopInput.activity)
        val stratum: Stratum = getStratum(shopInput.stratum)
        val shopType: ShopType = getShopType(shopInput.shopType)

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
        ).transact(db)
    }

    def findActivityByName(name: String): IO[Option[Activity]] =
        Query.findActivityByName(name).transact(db)

    def findStratumByName(name: String): IO[Option[Stratum]] =
        Query.findStratumByName(name).transact(db)

    def findShopTypeByName(name: String): IO[Option[ShopType]] =
        Query.findShopTypeByName(name).transact(db)

    def insertActivity(name: String): IO[Activity] =
        Query.insertActivity(name).transact(db)

    def insertStratum(name: String): IO[Stratum] =
        Query.insertStratum(name).transact(db)

    def insertShopType(name: String): IO[ShopType] =
        Query.insertShopType(name).transact(db)
}