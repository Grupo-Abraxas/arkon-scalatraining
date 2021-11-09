package scraper

import common.Models._
import common.GlobalConnection

import cats.effect._
import cats.implicits.catsStdInstancesForList

import doobie.implicits._
import doobie.util.update.Update
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

class ScraperRepository {
  val transactor = GlobalConnection().connect
  val inegiUrl = "https://www.inegi.org.mx/app/api/denue/v1/consulta/Buscar"
  val token = "1cc8124a-cf39-41f3-af93-2c7e89f5be30"

  def scrapeData(businessType: String, lat: String, long: String, radius: Int = 5000) = {
    println(s"businessType: $businessType => lat: $lat => long: $long => radius: $radius")
    val jsonRaw = requests.get(s"$inegiUrl/$businessType/$lat,$long/$radius/$token").text
    var shopsList = new ListBuffer[ShopScrap]()
    var activitiesMap = Map[String, Int]()
    var shopTypesMap = Map[String, Int]()
    var stratumsMap = Map[String, Int]()
    var activitiesList = ListBuffer[ComercialActivity]()
    var shopTypeList = ListBuffer[ShopType]()
    var stratumList = ListBuffer[Stratum]()

    val retrievedCa = activities.unsafeRunSync
    val retrievedSt = shopTypes.unsafeRunSync
    val retrievedStr = stratums.unsafeRunSync

    retrievedCa.foreach(a => activitiesMap += (a.name -> a.id.toInt))
    retrievedSt.foreach(sp => shopTypesMap += (sp.name -> sp.id.toInt))
    retrievedStr.foreach(s => stratumsMap += (s.name -> s.id.toInt))

    for (data <- jsonRaw.parseJson.convertTo[List[InegiData]]) {
      val aKey = a(activitiesMap, data.Clase_actividad, data)
      val spKey = a(shopTypesMap, data.Tipo, data)
      val sKey = a(stratumsMap, data.Estrato, data)

      val address = s"${data.Ubicacion}, ${data.Colonia}, ${data.Calle}, ${data.Num_Exterior}, ${data.Num_Interior}, ${data.CP}"
      shopsList += ShopScrap(data.Id.toInt, data.Nombre, data.Razon_social, aKey.toInt, sKey.toInt, address, data.Telefono,
        data.Correo_e, data.Sitio_internet, spKey.toInt, data.Longitud.toFloat, data.Latitud.toFloat)
    }

    activitiesMap.foreach({ case (name, id) => activitiesList += ComercialActivity(id, name) })
    shopTypesMap.foreach({ case (name, id) => shopTypeList += ShopType(id, name) })
    stratumsMap.foreach({ case (name, id) => stratumList += Stratum(id, name) })

    activitiesList = activitiesList.filterNot(ac => retrievedCa.contains(ac))
    shopTypeList = shopTypeList.filterNot(st => retrievedSt.contains(st))
    stratumList = stratumList.filterNot(str => retrievedStr.contains(str))
    shopsList = shopsList.filterNot(shop => shopsIds.unsafeRunSync.contains(shop.id))

    createAll(activitiesList.toList, shopTypeList.toList, stratumList.toList, shopsList.toList).unsafeToFuture
    /*jsonRaw.parseJson.convertTo[List[InegiData]]*/
    Response( s"${activitiesList.size} rows saved",
      s"${shopTypeList.size} rows saved",
      s"${stratumList.size} rows saved",
      s"${shopsList.size} rows saved")
  }

  def a(m: Map[String, Int], keyName: String, data: InegiData) = {
    m.get(keyName).map {
      key => key
    }.getOrElse {
      m += (keyName -> data.Id.toInt)
      m(keyName)
    }
  }


  def shopsIds() = {
    val query: doobie.ConnectionIO[List[Int]] =
      sql"""
    select id
    from shop
    """.query[Int].to[List]
    transactor.use(query.transact[IO])
  }

  def activities() = {
    val query: doobie.ConnectionIO[List[ComercialActivity]] =
      sql"""
    select id, name from comercial_activity
    """.query[ComercialActivity].to[List]
    transactor.use(query.transact[IO])
  }

  def shopTypes() = {
    val query: doobie.ConnectionIO[List[ShopType]] =
      sql"""
    select id, name from shop_type
    """.query[ShopType].to[List]
    transactor.use(query.transact[IO])
  }

  def stratums() = {
    val query: doobie.ConnectionIO[List[Stratum]] =
      sql"""
    select id, name from Stratum
    """.query[Stratum].to[List]
    transactor.use(query.transact[IO])
  }

  def createComercialActivity(activities: List[ComercialActivity]) = {
    val sql = "insert into comercial_activity (id, name) values (?, ?)"
    transactor.use(Update[ComercialActivity](sql).updateMany(activities).transact[IO])
  }

  def createShopType(shops: List[ShopType]) = {
    val sql = "insert into shop_type (id, name) values (?, ?)"
    transactor.use(Update[ShopType](sql).updateMany(shops).transact[IO])
  }

  def createStratum(stratums: List[Stratum]) = {
    val sql = "insert into stratum (id, name) values (?, ?)"
    transactor.use(Update[Stratum](sql).updateMany(stratums).transact[IO])
  }

  def createShops(shops: List[ShopScrap]) = {
    val sql = "insert into shop (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, " +
      "shop_type_id, position) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
      "ST_SetSRID(ST_Point(?,?), 4326)::geography)"
    transactor.use(Update[ShopScrap](sql).updateMany(shops).transact[IO])
  }

  def createAll(ca: List[ComercialActivity], st: List[ShopType], str: List[Stratum], sps: List[ShopScrap]) = {
    val rows = for {
      activities <- createComercialActivity(ca)
      shopTypes <- createShopType(st)
      stratums <- createStratum(str)
      shops <- createShops(sps)
    } yield activities + shopTypes + stratums + shops
    rows
  }
}
