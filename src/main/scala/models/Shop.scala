package models

case class Shop(
                 id: String,
                 name: String,
                 businessName: Option[String],
                 activityId: Option[String],
                 stratumId: Option[String],
                 address: String,
                 phoneNumber: Option[String],
                 email: Option[String],
                 website: Option[String],
                 shopTypeId: Option[String],
                 lat: Double,
                 long: Double
               )