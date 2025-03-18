package training.models

import org.postgresql.geometric.PGpoint

case class Shop(
  id: Int,
  name: String,
  businessName: Option[String],
  activityId: Int,
  stratumId: Int,
  address: String,
  phoneNumber: Option[String],
  email: Option[String],
  website: Option[String],
  shopTypeId: Int,
  position: PGpoint
)
