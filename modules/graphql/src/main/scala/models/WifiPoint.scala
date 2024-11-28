package models

import sangria.schema._

case class WifiPoint(
    id: String,
    program: String,
    installationDate: Option[String],
    latitude: Double,
    longitude: Double,
    neighborhood: String,
    municipality: String
)

object WifiPoint {
  val Type: ObjectType[Unit, WifiPoint] = ObjectType(
    "WifiPoint",
    fields[Unit, WifiPoint](
      Field("id", StringType, resolve = _.value.id),
      Field("program", StringType, resolve = _.value.program),
      Field("installationDate", OptionType(StringType), resolve = _.value.installationDate),
      Field("latitude", FloatType, resolve = _.value.latitude),
      Field("longitude", FloatType, resolve = _.value.longitude),
      Field("neighborhood", StringType, resolve = _.value.neighborhood),
      Field("municipality", StringType, resolve = _.value.municipality)
    )
  )

  val inputArguments: List[Argument[_]] = List(
    Argument("id", StringType),
    Argument("program", StringType),
    Argument("installationDate", OptionInputType(StringType)),
    Argument("latitude", FloatType),
    Argument("longitude", FloatType),
    Argument("neighborhood", StringType),
    Argument("municipality", StringType)
  )
}
