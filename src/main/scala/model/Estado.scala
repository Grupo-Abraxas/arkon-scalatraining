package model

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

case class Estado (id: Int, description :String)
object Person {
  implicit val decoder: Decoder[Estado] = io.circe.generic.semiauto.deriveDecoder
  implicit val encoder: Encoder[Estado] = io.circe.generic.semiauto.deriveEncoder
}
