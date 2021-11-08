package com.arkondata.training.dto

import spray.json.{DeserializationException, JsObject, JsString, JsValue, RootJsonFormat}

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
                          ) {
}


object InegiResponse {
  implicit object InegiResponseFormat extends RootJsonFormat[InegiResponse] {
    def write(c: InegiResponse): JsValue = JsObject(
      "CLEE" -> JsString( c.clee ),
      "Id" -> JsString( c.id ),
      "Nombre" -> JsString( c.nombre ),
      "Razon_social" -> JsString( c.razonSocial ),
      "Clase_actividad" -> JsString( c.claseActividad ),
      "Estrato" -> JsString( c.estrato ),
      "Tipo_vialidad" -> JsString( c.tipoVialidad ),
      "Calle" -> JsString( c.calle ),
      "Num_Exterior" -> JsString( c.numExterior ),
      "Num_Interior" -> JsString( c.numInterior ),
      "Colonia" -> JsString( c.colonia ),
      "CP" -> JsString( c.cp ),
      "Ubicacion" -> JsString( c.ubicacion ),
      "Telefono" -> JsString( c.telefono ),
      "Correo_e" -> JsString( c.correoE ),
      "Sitio_internet" -> JsString( c.sitioInternet ),
      "Tipo" -> JsString( c.tipo ),
      "Longitud" -> JsString( c.longitud ),
      "Latitud" -> JsString( c.latitud ),
      "tipo_corredor_industrial" -> JsString( c.tipoCorredorIndustrial ),
      "nom_corredor_industrial" -> JsString( c.nomCorredorIndustrial ),
      "numero_local" -> JsString( c.numeroLocal ),
    )
    def read(value: JsValue): InegiResponse = {
      value.asJsObject.getFields(
        "CLEE",
                  "Id",
                  "Nombre",
                  "Razon_social",
                  "Clase_actividad",
                  "Estrato",
                  "Tipo_vialidad",
                  "Calle",
                  "Num_Exterior",
                  "Num_Interior",
                  "Colonia",
                  "CP",
                  "Ubicacion",
                  "Telefono",
                  "Correo_e",
                  "Sitio_internet",
                  "Tipo",
                  "Longitud",
                  "Latitud",
                  "tipo_corredor_industrial",
                  "nom_corredor_industrial",
                  "numero_local"
      ) match {

        case Seq(
                JsString( clee ),
                JsString( id ),
                JsString( name ),
                JsString( razonSocial ),
                JsString( claseActividad ),
                JsString( estrato ),
                JsString( tipoVialidad ),
                JsString( calle ),
                JsString( numExterior ),
                JsString( numInterior ),
                JsString( colonia ),
                JsString( cp ),
                JsString( ubicacion ),
                JsString( telefono ),
                JsString( correoE ),
                JsString( sitioInternet ),
                JsString( tipo ),
                JsString( longitud ),
                JsString( latitud ),
                JsString( tipoCorredorIndustrial ),
                JsString( nomCorredorIndustrial ),
                JsString( numeroLocal )

        ) =>
          new InegiResponse( clee, id, name, razonSocial, claseActividad, estrato, tipoVialidad, calle,
            numExterior, numInterior, colonia, cp, ubicacion, telefono, correoE, sitioInternet, tipo, longitud, latitud,
            tipoCorredorIndustrial, nomCorredorIndustrial, numeroLocal)
        case _ => throw DeserializationException("ClassInfo expected")

      }
    }
  }
}