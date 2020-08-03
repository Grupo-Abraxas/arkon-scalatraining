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

  case class ComercialActivity(id: Int, name: String)

  case class ShopType(id: Int, name: String) {}

  case class Stratum(id: Int, name: String) {}

  object Shop {
    implicit val hasId = HasId[Shop, Int](_.id)
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
