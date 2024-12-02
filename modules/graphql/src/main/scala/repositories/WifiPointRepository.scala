package repositories

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import models.WifiPoint

class WifiPointRepository(transactor: Transactor[IO]) {

  def add(wifiPoint: WifiPoint): IO[Int] = {
    val insertQuery = wifiPoint.id match {
      case Some(id) =>
        sql"""
             |INSERT INTO wifi_points (id, program, installation_date, latitude, longitude, neighborhood, municipality)
             |VALUES ($id, ${wifiPoint.program}, ${wifiPoint.installationDate}, ${wifiPoint.latitude},
             |        ${wifiPoint.longitude}, ${wifiPoint.neighborhood}, ${wifiPoint.municipality})
        """.stripMargin
      case None =>
        sql"""
             |INSERT INTO wifi_points (program, installation_date, latitude, longitude, neighborhood, municipality)
             |VALUES (${wifiPoint.program}, ${wifiPoint.installationDate}, ${wifiPoint.latitude},
             |        ${wifiPoint.longitude}, ${wifiPoint.neighborhood}, ${wifiPoint.municipality})
             |RETURNING id
        """.stripMargin
    }

    insertQuery.update.withUniqueGeneratedKeys[Int]("id").transact(transactor)
  }

  def delete(id: Int): IO[Int] =
    sql"""
         |DELETE FROM wifi_points WHERE id = $id
    """.stripMargin.update.run.transact(transactor)

  def findAll(limit: Int, offset: Int): IO[List[WifiPoint]] =
    sql"""
         |SELECT * FROM wifi_points LIMIT $limit OFFSET $offset
    """.stripMargin
      .query[WifiPoint]
      .to[List]
      .transact(transactor)

  def findById(id: Int): IO[Option[WifiPoint]] =
    sql"""
         |SELECT * FROM wifi_points WHERE id = $id
    """.stripMargin
      .query[WifiPoint]
      .option
      .transact(transactor)

  def findByNeighborhood(neighborhood: String, limit: Int, offset: Int): IO[List[WifiPoint]] =
    sql"""
         |SELECT * FROM wifi_points
         |WHERE neighborhood = $neighborhood
         |LIMIT $limit OFFSET $offset
    """.stripMargin
      .query[WifiPoint]
      .to[List]
      .transact(transactor)

  def findNearby(latitude: Double, longitude: Double, distance: Double): IO[List[WifiPoint]] =
    sql"""
         |SELECT *, earth_distance(
         |  ll_to_earth($latitude, $longitude),
         |  ll_to_earth(latitude, longitude)
         |) AS distance
         |FROM wifi_points
         |WHERE earth_box(ll_to_earth($latitude, $longitude), $distance) @> ll_to_earth(latitude, longitude)
         |ORDER BY distance
    """.stripMargin
      .query[WifiPoint]
      .to[List]
      .transact(transactor)
}
