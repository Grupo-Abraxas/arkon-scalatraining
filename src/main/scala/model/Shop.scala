package training.model

case class Shop(
  id: Int,
  name: String,
  businessName: String,
  activityId: Int,
  stratumId: Int,
  address: String,
  phoneNumber: String,
  email: String,
  website: String,
  shopCategoryId: Int,
  longitude: Double,
  latitude: Double
)
