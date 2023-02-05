package database

import doobie._
import doobie.implicits._

import training.models.{Activity, Stratum, ShopType, Shop}

object Query {
    def findShopById(id: Long): ConnectionIO[Option[Shop]] =
        sql"""
            select
                s.id, s.name, s.business_name, ca.name as activity, st.name as stratum,
                s.address, s.phone_number, s.email, s.website, t.name as shop_type,
                ST_X(s.position::geometry) as longitude, ST_Y(s.position::geometry) as latitude
            from shop s
            join comercial_activity ca on ca.id = s.activity_id
            join stratum st on st.id = s.stratum_id
            join shop_type t on t.id = s.shop_type_id
            where s.id = $id
        """.query[Shop].option

    def listShops(limit: Int, offset: Int): ConnectionIO[List[Shop]] =
        sql"""
            select
                s.id, s.name, s.business_name, ca.name as activity, st.name as stratum,
                s.address, s.phone_number, s.email, s.website, t.name as shop_type,
                ST_X(s.position::geometry) as longitude, ST_Y(s.position::geometry) as latitude
            from shop s
            join comercial_activity ca on ca.id = s.activity_id
            join stratum st on st.id = s.stratum_id
            join shop_type t on t.id = s.shop_type_id
            limit $limit offset $offset
        """.query[Shop].to[List]

    def nearbyShops(limit: Int, lat: Double, lng: Double): ConnectionIO[List[Shop]] =
        sql"""
            select
                s.id, s.name, s.business_name, ca.name as activity, st.name as stratum,
                s.address, s.phone_number, s.email, s.website, t.name as shop_type,
                ST_X(s.position::geometry) as longitude, ST_Y(s.position::geometry) as latitude
            from shop s
            join comercial_activity ca on ca.id = s.activity_id
            join stratum st on st.id = s.stratum_id
            join shop_type t on t.id = s.shop_type_id
            order by st_distance(s.position, ST_MakePoint(${lng}, ${lat}))
            limit $limit
        """.query[Shop].to[List]

    def shopsInRadius(radius: Int, lat: Double, lng: Double): ConnectionIO[List[Shop]] =
        sql"""
            select
                s.id, s.name, s.business_name, ca.name as activity, st.name as stratum,
                s.address, s.phone_number, s.email, s.website, t.name as shop_type,
                ST_X(s.position::geometry) as longitude, ST_Y(s.position::geometry) as latitude
            from shop s
            join comercial_activity ca on ca.id = s.activity_id
            join stratum st on st.id = s.stratum_id
            join shop_type t on t.id = s.shop_type_id
            where ST_DWithin(s.position, ST_MakePoint(${lng}, ${lat}), $radius)
            order by st_distance(s.position, ST_MakePoint(${lng}, ${lat}))
        """.query[Shop].to[List]

    def findActivityByName(name: String): ConnectionIO[Option[Activity]] =
        sql"select id, name from comercial_activity where name=$name".query[Activity].option
    
    def findStratumByName(name: String): ConnectionIO[Option[Stratum]] =
        sql"select id, name from stratum where name=$name".query[Stratum].option

    def findShopTypeByName(name: String): ConnectionIO[Option[ShopType]] =
        sql"select id, name from shop_type where name=$name".query[ShopType].option
    
    def insertActivity(name: String): ConnectionIO[Activity] =
        for {
            _  <- sql"insert into comercial_activity (name) values ($name)".update.run
            id <- sql"select lastval()".query[Long].unique
            p  <- sql"select id, name from comercial_activity where id = $id".query[Activity].unique
        } yield p

    def insertStratum(name: String): ConnectionIO[Stratum] =
        for {
            _  <- sql"insert into stratum (name) values ($name)".update.run
            id <- sql"select lastval()".query[Long].unique
            p  <- sql"select id, name from stratum where id = $id".query[Stratum].unique
        } yield p

    def insertShopType(name: String): ConnectionIO[ShopType] =
        for {
            _  <- sql"insert into shop_type (name) values ($name)".update.run
            id <- sql"select lastval()".query[Long].unique
            p  <- sql"select id, name from shop_type where id = $id".query[ShopType].unique
        } yield p
    
    def insertShop(
        name: String,
        businessName: Option[String],
        activityId: Int,
        stratumId: Int,
        address: String,
        phoneNumber: Option[String],
        email: Option[String],
        website: Option[String],
        shopTypeId: Int,
        longitude: Double, 
        latitude: Double
    ): ConnectionIO[Shop] = 
        for {
            id  <- sql"""
                    insert into shop 
                    (name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
                    values ($name, $businessName, $activityId, $stratumId, $address, $phoneNumber, $email, $website, $shopTypeId, 
                    ST_MakePoint(${longitude}, ${latitude}))
                """.update.withUniqueGeneratedKeys[Long]("id")
            p  <- sql"""
                    select
                        s.id, s.name, s.business_name, ca.name as activity, st.name as stratum,
                        s.address, s.phone_number, s.email, s.website, t.name as shop_type,
                        ST_X(s.position::geometry) as longitude, ST_Y(s.position::geometry) as latitude
                    from shop s
                    join comercial_activity ca on ca.id = s.activity_id
                    join stratum st on st.id = s.stratum_id
                    join shop_type t on t.id = s.shop_type_id
                    where s.id = $id
                """.query[Shop].unique
        } yield p
    
}
