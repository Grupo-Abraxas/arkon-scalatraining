package graphql

case class CreateShopInput(
                            id: Int,
                            name: String,
                            businessName: Option[String],
                            activity: Option[String],
                            stratum: Option[String],
                            address: String,
                            phoneNumber: Option[String],
                            email: Option[String],
                            website: Option[String],
                            shopType: Option[String],
                            lat: Float,
                            long: Float,
                          )
