package model

import io.circe.{Decoder, Encoder}

case class Unidad(id: Int, vehicle_id: Int, point_latitude: String, point_longitude: String, geopoint: String, estado: Int)
object Unidad {
  implicit val decoder: Decoder[Estado] = io.circe.generic.semiauto.deriveDecoder
  implicit val encoder: Encoder[Estado] = io.circe.generic.semiauto.deriveEncoder
}