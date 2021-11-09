package common

import sangria.execution.deferred.HasId
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
                       Ubicacion: String)

  case class ShopScrap(id: Int,
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
                       long: Float)

  case class Response(comercialActivity: String, shopType: String, stratum: String, shop: String)

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
                  position: String) {}

  case class Position(x: Float, y: Float) {}

  case class CreateShopInput(id: Int,
                             name: String,
                             businessName: String,
                             activity: String,
                             stratum: String,
                             address: String,
                             phoneNumber: String,
                             email: String,
                             website: String,
                             shopType: String,
                             lat: Float,
                             long: Float) {
  }

  case class CreateShopPayload(id: Int) {}

  case class ComercialActivity(id: Int, name: String)

  case class ShopType(id: Int, name: String) {}

  case class Stratum(id: Int, name: String) {}

  object CreateShopInput {
    implicit val hasId = HasId[Shop, Float](_.id)
  }

  object Shop {
    implicit val hasId = HasId[Shop, Int](_.id)
    implicit val hasPosition = HasId[Shop, String](_.position)
  }

  object ComercialActivity {
    implicit val hasId = HasId[ComercialActivity, Int](_.id)
  }

  object ShopType {
    implicit val hasId = HasId[ShopType, Int](_.id)
  }

  object Stratum {
    implicit val hasId = HasId[Stratum, Int](_.id)
  }


}
