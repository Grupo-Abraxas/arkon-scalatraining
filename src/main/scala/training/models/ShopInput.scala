package training.models

case class ShopInput(
    name: String,
    businessName: Option[String],
    activity: String,
    stratum: String,
    address: String,
    phoneNumber: Option[String],
    email: Option[String],
    website: Option[String],
    shopType: String,
    longitude: Double,
    latitude: Double
)