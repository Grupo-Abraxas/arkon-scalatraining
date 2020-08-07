package training.std

import sangria.execution.deferred.HasId

object Models {

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
