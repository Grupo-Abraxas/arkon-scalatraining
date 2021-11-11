package com.arkondata.training.dto

import io.circe._


case class InegiResponse(
                          clee : String,
                          id : String,
                          nombre : String,
                          razonSocial : String,
                          claseActividad : String,
                          estrato : String,
                          tipoVialidad : String,
                          calle : String,
                          numExterior : String,
                          numInterior : String,
                          colonia : String,
                          cp : String,
                          ubicacion : String,
                          telefono : String,
                          correoE : String,
                          sitioInternet : String,
                          tipo : String,
                          longitud : String,
                          latitud : String,
                          tipoCorredorIndustrial : String,
                          nomCorredorIndustrial : String,
                          numeroLocal : String
                          )

object InegiResponse {


  implicit val decode0: Decoder[InegiResponse] = (hCursor: HCursor) =>
    for {
      clee <- hCursor.get[ String ]( "CLEE" )
      id <- hCursor.get[String]( "Id" )
      nombre <- hCursor.get[String]( "Nombre" )
      razonSocial <- hCursor.get[String]( "Razon_social" )
      claseActividad <- hCursor.get[String]( "Clase_actividad" )
      estrato <- hCursor.get[String]( "Estrato" )
      tipoVialidad <- hCursor.get[String]( "Tipo_vialidad" )
      calle <- hCursor.get[String]( "Calle" )
      numExterior <- hCursor.get[String]( "Num_Exterior" )
      numInterior <- hCursor.get[String]( "Num_Interior" )
      colonia <- hCursor.get[String]( "Colonia" )
      cp <- hCursor.get[String]( "CP" )
      ubicacion <- hCursor.get[String]( "Ubicacion" )
      telefono <- hCursor.get[String]( "Telefono" )
      correoE <- hCursor.get[String]( "Correo_e" )
      sitioInternet <- hCursor.get[String]( "Sitio_internet" )
      tipo <- hCursor.get[String]( "Tipo" )
      latitud <- hCursor.get[String]( "Longitud" )
      longitud <- hCursor.get[String]( "Latitud" )
      tipoCorredorIndustrial <- hCursor.get[String]( "tipo_corredor_industrial" )
      nomCorredorIndustrial <- hCursor.get[String]( "nom_corredor_industrial" )
      numeroLocal <- hCursor.get[String]( "numero_local" )

    } yield InegiResponse( clee, id, nombre, razonSocial , claseActividad , estrato , tipoVialidad , calle ,
      numExterior , numInterior , colonia , cp , ubicacion , telefono , correoE , sitioInternet , tipo ,
      longitud , latitud , tipoCorredorIndustrial , nomCorredorIndustrial , numeroLocal  )


}
