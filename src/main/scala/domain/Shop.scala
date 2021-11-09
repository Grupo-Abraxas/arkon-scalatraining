package domain

case class Shop(
                 id: Int,
                 name: String,
                 businessName: Option[String],
                 activity: Option[Activity],
                 stratum: Option[Stratum],
                 address: String,
                 phoneNumber: Option[String],
                 email: Option[String],
                 website: Option[String],
                 shopType: Option[ShopType],
                 lat: Float,
                 long: Float,
                 nerabyShops: List[Shop],
                 shopsInRadius: List[Shop],
               )
