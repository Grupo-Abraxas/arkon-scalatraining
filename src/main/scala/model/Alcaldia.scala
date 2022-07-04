package model

import io.circe.{Decoder, Encoder}

case class Alcaldia(id: Int, name: String, geopolygon: String, estado: Int)
object Alcaldia {
  implicit val decoder: Decoder[Estado] = io.circe.generic.semiauto.deriveDecoder
  implicit val encoder: Encoder[Estado] = io.circe.generic.semiauto.deriveEncoder
}