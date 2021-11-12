package training.model

final case class Shop(
    id: Int,
    activity: Activity,
    address: String,
    email: String,
    businessName: String,
    lat: Float,
    long: Float,
    name: String,
    phoneNumber: String,
    shopType: ShopType,
    stratum: Stratum,
    website: String
)
