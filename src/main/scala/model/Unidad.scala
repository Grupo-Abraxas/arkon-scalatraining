package model

import io.circe.{Decoder, Encoder}
import sangria.execution.deferred.HasId

case class Unidad(id: Int, vehicle_id: Int, point_latitude: String, point_longitude: String, geopoint: String, estado: Int)
object Unidad {
  implicit val decoder: Decoder[Unidad] = io.circe.generic.semiauto.deriveDecoder[Unidad]
  implicit val encoder: Encoder[Unidad] = io.circe.generic.semiauto.deriveEncoder[Unidad]
  implicit val hasId = HasId[Unidad, Int](_.id)
}