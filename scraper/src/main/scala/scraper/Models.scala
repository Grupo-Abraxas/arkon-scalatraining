package scraper

import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat

object Models {
  implicit val ResponseFormat: JsonFormat[InegiData] = jsonFormat21(InegiData)

  case class InegiData(CP: String,
                       Calle: String,
                       CentroComercial: String,
                       Clase_actividad: String,
                       Colonia: String,
                       Correo_e: String,
                       Estrato: String,
                       Id: String,
                       Latitud: String,
                       Longitud: String,
                       Nombre: String,
                       NumLocal: String,
                       Num_Exterior: String,
                       Num_Interior: String,
                       Razon_social: String,
                       Sitio_internet: String,
                       Telefono: String,
                       Tipo: String,
                       TipoCentroComercial: String,
                       Tipo_vialidad: String,
                       Ubicacion: String
                      )

  case class Shop(id: Int,
                  name: String,
                  business_name: String,
                  activity_id: Int,
                  stratum_id: Int,
                  address: String,
                  phone_number: String,
                  email: String,
                  website: String,
                  shop_type_id: Int,
                  lat: Float,
                  long: Float) {}

  case class ComercialActivity(id: Int, name: String)

  case class ShopType(id: Int, name: String)

  case class Stratum(id: Int, name: String)

  case class Response(comercialActivity: String, shopType: String, stratum: String, shop: String)

}
