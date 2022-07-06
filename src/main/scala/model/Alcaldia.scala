package model

import io.circe.{Decoder, Encoder}
import sangria.execution.deferred.HasId

case class Alcaldia(id: Int, name: String, geopolygon: String, estado: Int)
object Alcaldia {
  implicit val decoder: Decoder[Alcaldia] = io.circe.generic.semiauto.deriveDecoder[Alcaldia]
  implicit val encoder: Encoder[Alcaldia] = io.circe.generic.semiauto.deriveEncoder[Alcaldia]
  implicit val hasId = HasId[Alcaldia, Int](_.id)

}