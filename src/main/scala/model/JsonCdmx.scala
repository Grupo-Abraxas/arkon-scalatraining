package model

import io.circe.{Codec, Decoder}
import io.circe.syntax._
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder}


object JsonCdmx {
  case class AlcaldiaJson(id: Int, nomgeo: String, geo_shape: String)

  case class UnidadJson( id: Int, vehicle_id: Int, position_latitude: Double, position_longitude: Double,
                        geographic_point: String)

  object AlcaldiaJson {
    implicit val AlcaldiaJsonDecoder: Decoder[AlcaldiaJson] = deriveDecoder[AlcaldiaJson]

  }
  object UnidadJson {
    implicit val UnidadJsonDecoder: Decoder[UnidadJson] = deriveDecoder[UnidadJson]
  }

}
