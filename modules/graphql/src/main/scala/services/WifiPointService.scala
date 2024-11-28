package services

import cats.effect.IO
import models.WifiPoint
import repositories.WifiPointRepository

class WifiPointService(repository: WifiPointRepository) {

  def addWifiPoint(
      id: String,
      program: String,
      installationDate: Option[String],
      latitude: Double,
      longitude: Double,
      neighborhood: String,
      municipality: String
  ): WifiPoint = {
    val wifiPoint =
      WifiPoint(id, program, installationDate, latitude, longitude, neighborhood, municipality)
    repository.add(wifiPoint)
    wifiPoint
  }

  def deleteWifiPoint(id: String): String = {
    repository.delete(id)
    s"WifiPoint with id $id has been deleted."
  }

  def getWifiPoints(limit: Int, offset: Int): IO[List[WifiPoint]] =
    repository.findAll(limit, offset)

  def getWifiPointById(id: String): IO[Option[WifiPoint]] =
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
