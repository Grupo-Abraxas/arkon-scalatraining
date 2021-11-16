package com.arkondata.training.model


import io.circe._

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


object InegiResponse {


  implicit val decode2: Decoder[CreateShopInput] = (hCursor: HCursor) =>
    for {

      id <- hCursor.get[Int]( "Id" )
      name <- hCursor.get[String]( "Nombre" )
      businessName <- hCursor.get[String]( "Razon_social" )
      activity <- hCursor.get[String]( "Clase_actividad" )
      stratum <- hCursor.get[String]( "Estrato" )
      address <- hCursor.get[String]( "Ubicacion" )
      phoneNumber <- hCursor.get[String]( "Telefono" )
      email <- hCursor.get[String]( "Correo_e" )
      webSite <- hCursor.get[String]( "Sitio_internet" )
      shopType <- hCursor.get[String]( "Tipo" )
      latitud <- hCursor.get[Double]( "Longitud" )
      longitud <- hCursor.get[Double]( "Latitud" )

    } yield CreateShopInput(  id, name, businessName , activity , stratum  , address, phoneNumber, email, webSite, shopType, latitud, longitud  )


}
