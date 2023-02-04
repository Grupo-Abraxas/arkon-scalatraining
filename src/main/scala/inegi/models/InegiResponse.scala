package inegi.models

case class InegiResponse(
    CLEE: String,
    Id: String,
    Nombre: String,
    Razon_social: Option[String],
    Clase_actividad: String,
    Estrato: String,
    Tipo_vialidad: String,
    Calle: String,
    Num_Exterior: String,
    Num_Interior: String,
    Colonia: String,
    CP: String,
    Ubicacion: String,
    Telefono: Option[String],
    Correo_e: Option[String],
    Sitio_internet: Option[String],
    Tipo: String,
    Longitud: String,
    Latitud: String,
    CentroComercial: Option[String],
    TipoCentroComercial: Option[String],
    NumLocal: Option[String]
)
