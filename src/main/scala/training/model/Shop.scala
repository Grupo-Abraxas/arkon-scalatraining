package training.model

case class Shop(
    id: String,
    address: String,
    businessName: Option[String],
    email: Option[String],
    lat: Double,
    long: Double,
    name: String,
    phoneNumber: Option[String],
    website: Option[String],
    activity: Option[String],
    shopType: Option[String],
    stratum: Option[String]
)
