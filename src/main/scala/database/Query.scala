package database

import doobie._
import doobie.implicits._

import training.models.{Activity, Stratum, ShopType, Shop}

/** Fabrica para instancias de [[database.Query]]. */
object Query {
    /** Realiza una consulta de establecimientos por ID
     *
     *  @param id identifiddor del Establecimiento
     *  @return IO expandible a Establecimiento opcional.
     */
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
    
    /** Realiza una consulta de establecimientos por limit y offset
     *
     *  @param limit número máximo de registros
     *  @param offset primer registro
     *  @return IO expandible a Lista de Establecimientos.
     */
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

    /** Realiza una consulta de establecimientos cercanos
     *
     *  @param limit número máximo de registros
     *  @param lat latitud del punto de entrada
     *  @param lng longitud del punto de entrada
     *  @return IO expandible a Lista de Establecimientos.
     */
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

    /** Realiza una consulta de establecimientos en un radio
     *
     *  @param radius radio en metros a buscar
     *  @param lat latitud del punto de entrada
     *  @param lng longitud del punto de entrada
     *  @return IO expandible a Lista de Establecimientos.
     */
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
    
    /** Realiza una consulta de actividad económica por nombre
     *
     *  @param name nombre de la actividad económica
     *  @return IO expandible a Actividad opcional.
     */
    def findActivityByName(name: String): ConnectionIO[Option[Activity]] =
        sql"select id, name from comercial_activity where name=$name".query[Activity].option
    
    /** Realiza una consulta de estrato por nombre
     *
     *  @param name nombre del estrato
     *  @return IO expandible a Estrato opcional.
     */
    def findStratumByName(name: String): ConnectionIO[Option[Stratum]] =
        sql"select id, name from stratum where name=$name".query[Stratum].option

    /** Realiza una consulta de Tipo de Establecimiento por nombre
     *
     *  @param name nombre del Tipo de Establecimiento
     *  @return IO expandible a Tipo de Establecimiento opcional.
     */
    def findShopTypeByName(name: String): ConnectionIO[Option[ShopType]] =
        sql"select id, name from shop_type where name=$name".query[ShopType].option

    /** Inserta una actividad económica
     *
     *  @param name nombre de la actividad económica
     *  @return IO expandible a Actividad.
     */
    def insertActivity(name: String): ConnectionIO[Activity] =
        for {
            id  <- sql"insert into comercial_activity (name) values ($name)".update.withUniqueGeneratedKeys[Long]("id")
            p  <- sql"select id, name from comercial_activity where id = $id".query[Activity].unique
        } yield p

    /** Inserta un Estrato
     *
     *  @param name nombre del Estrato
     *  @return IO expandible a Estrato.
     */
    def insertStratum(name: String): ConnectionIO[Stratum] =
        for {
            id  <- sql"insert into stratum (name) values ($name)".update.withUniqueGeneratedKeys[Long]("id")
            p  <- sql"select id, name from stratum where id = $id".query[Stratum].unique
        } yield p

    /** Inserta un Tipo de Establecimiento
     *
     *  @param name nombre del Tipo de Establecimiento
     *  @return IO expandible a Tipo de Establecimiento.
     */
    def insertShopType(name: String): ConnectionIO[ShopType] =
        for {
            id  <- sql"insert into shop_type (name) values ($name)".update.withUniqueGeneratedKeys[Long]("id")
            p  <- sql"select id, name from shop_type where id = $id".query[ShopType].unique
        } yield p
    
    /** Inserta unn Establecimiento.
     *
     *  @param id identificador del Establecimiento
     *  @param name nombre del Establecimiento
     *  @param businessName Razón social
     *  @param activity Clase de la actividad económica
     *  @param stratum Estrato (Personal ocupado)
     *  @param address domicilio del Establecimiento
     *  @param phoneNumber Teléfono
     *  @param email Correo electrónico
     *  @param website Página de internet
     *  @param shopType Tipo de establecimiento
     *  @param longitude Coordenadas del Establecimiento
     *  @param latitude Coordenadas del Establecimiento
     *  @return IO expandible a Establecimiento.
     */
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
