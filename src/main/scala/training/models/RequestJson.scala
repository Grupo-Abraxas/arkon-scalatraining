package training.models

import io.circe._

case class RequestJson(operationName: Option[String], variables: Json, query: String)
