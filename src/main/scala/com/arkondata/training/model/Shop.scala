package com.arkondata.training.model



case class Shop (
                  id: Int,
                  name: String,
                  businessName: String,
                  activityId: Int,
                  address: String,
                  phoneNumber: String,
                  email: String,
                  website: String,
                  shopTypeId: Int,
                  stratumId: Int,
                  lat: Double,
                  long: Double
                )
