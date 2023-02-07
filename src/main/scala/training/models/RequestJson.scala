package training.models

import io.circe._

/** Una solicitud JSON a la API de graphql.
 *
 *  @constructor crea una solicitud JSON a la API de graphql
 *  @param operationName el nombre de la operación
 *  @param variables las variables enviadas
 *  @param query la query enviada
 */
case class RequestJson(operationName: Option[String], variables: Json, query: String)
