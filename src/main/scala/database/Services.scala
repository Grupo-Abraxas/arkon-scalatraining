package database

import cats.effect._
import cats.effect.unsafe.implicits.global

import doobie.implicits._
import doobie.hikari.HikariTransactor

import training.models.{Activity, Stratum, ShopType, Shop, ShopInput}

/** Servicios.
 *
 *  @constructor crea los Servicios de acceso a base de datos
 *  @param db transactor de base de datos
 */
class Services(db: HikariTransactor[IO]) {
    /** Realiza una consulta de establecimientos por ID
     *
     *  @param id identifiddor del Establecimiento
     *  @return IO expandible a Establecimiento opcional.
     */
    def findShopById(id: Long): IO[Option[Shop]] =
        Query.findShopById(id).transact(db)

    /** Realiza una consulta de establecimientos por limit y offset
     *
     *  @param limit número máximo de registros
     *  @param offset primer registro
     *  @return IO expandible a Lista de Establecimientos.
     */
    def listShops(limit: Int, offset: Int): IO[List[Shop]] =
        Query.listShops(limit, offset).transact(db)
    
    /** Realiza una consulta de establecimientos cercanos
     *
     *  @param limit número máximo de registros
     *  @param lat latitud del punto de entrada
     *  @param lng longitud del punto de entrada
     *  @return IO expandible a Lista de Establecimientos.
     */
    def nearbyShops(limit: Int, lat: Double, lng: Double): IO[List[Shop]] =
        Query.nearbyShops(limit, lat, lng).transact(db)
    
    /** Realiza una consulta de establecimientos en un radio
     *
     *  @param radius radio en metros a buscar
     *  @param lat latitud del punto de entrada
     *  @param lng longitud del punto de entrada
     *  @return IO expandible a Lista de Establecimientos.
     */
    def shopsInRadius(radius: Int, lat: Double, lng: Double): IO[List[Shop]] =
        Query.shopsInRadius(radius, lat, lng).transact(db)
    
    /** Obtiene/Crea una actividad económica por nombre
     *
     *  @param name nombre de la actividad económica
     *  @return Actividad económica.
     */
    def getActivity(name: String): Activity = {
        val findActivity: Option[Activity] = findActivityByName(name).unsafeRunSync()
        findActivity getOrElse insertActivity(name).unsafeRunSync()
    }

    /** Obtiene/Crea un estrato por nombre
     *
     *  @param name nombre del estrato
     *  @return Estrato.
     */
    def getStratum(name: String): Stratum = {
        val findStratum: Option[Stratum] = findStratumByName(name).unsafeRunSync()
        findStratum getOrElse insertStratum(name).unsafeRunSync()
    }

    /** Obtiene/Crea un tipo de establecimiento
     *
     *  @param name nombre del tipo de establecimiento
     *  @return Tipo de establecimiento.
     */
    def getShopType(name: String): ShopType = {
        val findShopType: Option[ShopType] = findShopTypeByName(name).unsafeRunSync()
        findShopType getOrElse insertShopType(name).unsafeRunSync()
    }

    /** Inserta unn Establecimiento.
     *
     *  @param shopInput formulario de entrada del Establecimiento
     *  @return IO expandible a Establecimiento.
     */
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

    /** Realiza una consulta de actividad económica por nombre
     *
     *  @param name nombre de la actividad económica
     *  @return IO expandible a Actividad opcional.
     */
    def findActivityByName(name: String): IO[Option[Activity]] =
        Query.findActivityByName(name).transact(db)
    
    /** Realiza una consulta de estrato por nombre
     *
     *  @param name nombre del estrato
     *  @return IO expandible a Estrato opcional.
     */
    def findStratumByName(name: String): IO[Option[Stratum]] =
        Query.findStratumByName(name).transact(db)

    /** Realiza una consulta de Tipo de Establecimiento por nombre
     *
     *  @param name nombre del Tipo de Establecimiento
     *  @return IO expandible a Tipo de Establecimiento opcional.
     */
    def findShopTypeByName(name: String): IO[Option[ShopType]] =
        Query.findShopTypeByName(name).transact(db)
    
    /** Inserta una actividad económica
     *
     *  @param name nombre de la actividad económica
     *  @return IO expandible a Actividad.
     */
    def insertActivity(name: String): IO[Activity] =
        Query.insertActivity(name).transact(db)
    
    /** Inserta un Estrato
     *
     *  @param name nombre del Estrato
     *  @return IO expandible a Estrato.
     */
    def insertStratum(name: String): IO[Stratum] =
        Query.insertStratum(name).transact(db)
    
    /** Inserta un Tipo de Establecimiento
     *
     *  @param name nombre del Tipo de Establecimiento
     *  @return IO expandible a Tipo de Establecimiento.
     */
    def insertShopType(name: String): IO[ShopType] =
        Query.insertShopType(name).transact(db)
}