package training.models

case class Shop(
    id: Long,
    name: String,
    business_name: Option[String],
    activity: String,
    stratum: String,
    address: String,
    phone_number: Option[String],
    email: Option[String],
    website: Option[String],
    shop_type: String,
    longitude: Double,
    latitude: Double
)