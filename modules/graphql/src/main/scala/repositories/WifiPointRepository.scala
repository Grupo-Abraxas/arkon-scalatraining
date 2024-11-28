package repositories

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import models.WifiPoint

class WifiPointRepository(transactor: Transactor[IO]) {

  def add(wifiPoint: WifiPoint): IO[Int] =
    sql"""
      INSERT INTO wifi_points (id, program, installation_date, latitude, longitude, neighborhood, municipality)
      VALUES (${wifiPoint.id}, ${wifiPoint.program}, ${wifiPoint.installationDate}, ${wifiPoint.latitude},
              ${wifiPoint.longitude}, ${wifiPoint.neighborhood}, ${wifiPoint.municipality})
    """.update.run
      .transact(transactor)

  def delete(id: String): IO[Int] =
    sql"DELETE FROM wifi_points WHERE id = $id".update.run
      .transact(transactor)

  def findAll(limit: Int, offset: Int): IO[List[WifiPoint]] =
    sql"SELECT * FROM wifi_points LIMIT $limit OFFSET $offset"
      .query[WifiPoint]
      .to[List]
      .transact(transactor)

  def findById(id: String): IO[Option[WifiPoint]] =
    sql"SELECT * FROM wifi_points WHERE id = $id"
      .query[WifiPoint]
      .option
      .transact(transactor)

  def findByNeighborhood(neighborhood: String, limit: Int, offset: Int): IO[List[WifiPoint]] =
    sql"""
      SELECT * FROM wifi_points
      WHERE neighborhood = $neighborhood
      LIMIT $limit OFFSET $offset
    """
      .query[WifiPoint]
      .to[List]
      .transact(transactor)

  def findNearby(latitude: Double, longitude: Double, distance: Double): IO[List[WifiPoint]] =
    sql"""
      SELECT *, earth_distance(
        ll_to_earth($latitude, $longitude),
        ll_to_earth(latitude, longitude)
      ) AS distance
      FROM wifi_points
      WHERE earth_box(ll_to_earth($latitude, $longitude), $distance) @> ll_to_earth(latitude, longitude)
      ORDER BY distance
    """
      .query[WifiPoint]
      .to[List]
      .transact(transactor)
}
