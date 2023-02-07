package training.models

/** Un Establecimiento.
 *
 *  @constructor crea un Establecimiento
 *  @param id identificador del Establecimiento
 *  @param name nombre del Establecimiento
 *  @param businessName Razón social
 *  @param activity Clase de la actividad económica
 *  @param stratum Estrato (Personal ocupado)
 *  @param address domicilio del Establecimiento
 *  @param phoneNumber Teléfono
 *  @param email Correo electrónico
 *  @param website Página de internet
 *  @param shopType Tipo de establecimiento
 *  @param longitude Coordenadas del Establecimiento
 *  @param latitude Coordenadas del Establecimiento
 */
case class Shop(
    id: Long,
    name: String,
    businessName: Option[String],
    activity: String,
    stratum: String,
    address: String,
    phoneNumber: Option[String],
    email: Option[String],
    website: Option[String],
    shopType: String,
    longitude: Double,
    latitude: Double
)