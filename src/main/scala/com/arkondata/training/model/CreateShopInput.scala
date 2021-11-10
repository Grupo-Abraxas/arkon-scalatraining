package com.arkondata.training.model

case class CreateShopInput (
      id: Int,
      name: String,
      businessName: String,
      activity: String,
      stratum: String,
      address: String,
      phoneNumber: String,
      email: String,
      website: String,
      shopType: String,
      lat: Double,
      long: Double
  )
