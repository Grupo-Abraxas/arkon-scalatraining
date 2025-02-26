package services

import cats.effect.IO
import models.WifiPoint
import repositories.WifiPointRepository

class WifiPointService(repository: WifiPointRepository) {

  def addWifiPoint(
                    id: Option[Int] = None,
                    program: String,
                    installationDate: Option[String],
                    latitude: Double,
                    longitude: Double,
                    neighborhood: String,
                    municipality: String
                  ): IO[WifiPoint] = {
    val wifiPoint =
      WifiPoint(id, program, installationDate, latitude, longitude, neighborhood, municipality)

    for {
      _ <- repository.add(wifiPoint)
    } yield wifiPoint
  }

  def deleteWifiPoint(id: Int): IO[String] =
    repository.delete(id).map(_ => s"WifiPoint with id $id has been deleted.")

  def getWifiPoints(limit: Int, offset: Int): IO[List[WifiPoint]] =
    repository.findAll(limit, offset)

  def getWifiPointById(id: Int): IO[Option[WifiPoint]] =
    repository.findById(id)

  def getWifiPointsByNeighborhood(
                                   neighborhood: String,
                                   limit: Int,
                                   offset: Int
                                 ): IO[List[WifiPoint]] =
    repository.findByNeighborhood(neighborhood, limit, offset)

  def getWifiPointsByProximity(
                                latitude: Double,
                                longitude: Double,
                                distance: Double
                              ): IO[List[WifiPoint]] =
    repository.findNearby(latitude, longitude, distance)
}
