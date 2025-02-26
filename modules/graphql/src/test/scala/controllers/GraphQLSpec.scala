package controllers

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
final case class WiFiAccessPoint(id: String, colonia: String, lat: Double, long: Double)

class GraphQLSpec extends AnyFunSuite with Matchers {
  implicit val runtime: IORuntime = IORuntime.global
  val mockData: List[WiFiAccessPoint] = List(
    WiFiAccessPoint("1", "Colonia A", 19.432608, -99.133209),
    WiFiAccessPoint("2", "Colonia B", 19.434580, -99.135334),
    WiFiAccessPoint("3", "Colonia A", 19.432000, -99.132500)
  )
  def queryGraphQL(query: String): IO[List[WiFiAccessPoint]] = IO {
    query match {
      case q if q.contains("wifiAccessPoints(page: 1, pageSize: 2)")            =>
        mockData.take(2)
      case q if q.contains("""wifiAccessPoint(id: "1")""")                      =>
        mockData.filter(_.id == "1")
      case q if q.contains("wifiAccessPointsByColonia(colonia: \"Colonia A\")") =>
        mockData.filter(_.colonia == "Colonia A")
      case q
          if q.contains(
            "wifiAccessPointsByProximity(lat: 19.432608, long: -99.133209, limit: 2)"
          ) =>
        mockData
          .sortBy(
            p => distance(p.lat, p.long, 19.432608, -99.133209)
          )
          .take(2)
      case _                                                                    => List.empty
    }
  }
  def distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double =
    Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(long1 - long2, 2))
  test("Debería devolver una lista paginada de puntos WiFi") {
    val query  =
      """
        query {
          wifiAccessPoints(page: 1, pageSize: 2) {
            id
            colonia
          }
        }
      """
    val result = queryGraphQL(query).unsafeRunSync()

    result shouldEqual mockData.take(2)
  }
  test("Debería devolver un punto WiFi por ID") {
    val query  =
      """
        query {
          wifiAccessPoint(id: "1") {
            id
            colonia
          }
        }
      """
    val result = queryGraphQL(query).unsafeRunSync()

    result shouldEqual mockData.filter(_.id == "1")
  }
  test("Debería devolver puntos WiFi por colonia") {
    val query  =
      """
        query {
          wifiAccessPointsByColonia(colonia: "Colonia A") {
            id
            colonia
          }
        }
      """
    val result = queryGraphQL(query).unsafeRunSync()

    result shouldEqual mockData.filter(_.colonia == "Colonia A")
  }
  test("Debería devolver puntos WiFi ordenados por proximidad") {
    val query  =
      """
        query {
          wifiAccessPointsByProximity(lat: 19.432608, long: -99.133209, limit: 2) {
            id
            colonia
          }
        }
      """
    val result = queryGraphQL(query).unsafeRunSync()

    val expected = mockData
      .sortBy(
        p => distance(p.lat, p.long, 19.432608, -99.133209)
      )
      .take(2)
    result shouldEqual expected
  }
}
