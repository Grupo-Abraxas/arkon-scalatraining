package controllers

import models.WifiPoint
import sangria.schema._
import services.WifiPointService
import cats.effect.unsafe.IORuntime

class QueryController(service: WifiPointService)(implicit runtime: IORuntime) {
  val QueryType: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field(
        name = "hello",
        fieldType = StringType,
        resolve = _ => "GraphQL Server is running"
      ),
      Field(
        name = "wifiPoints",
        fieldType = ListType(WifiPoint.Type),
        arguments = List(Argument("limit", IntType), Argument("offset", IntType)),
        resolve = ctx => {
          val limit  = ctx.arg[Int]("limit")
          val offset = ctx.arg[Int]("offset")
          service.getWifiPoints(limit, offset).unsafeToFuture()
        }
      ),
      Field(
        name = "wifiPointById",
        fieldType = OptionType(WifiPoint.Type),
        arguments = List(Argument("id", IntType)),
        resolve = ctx => {
          val id = ctx.arg[Int]("id")
          service.getWifiPointById(id).unsafeToFuture()
        }
      ),
      Field(
        name = "wifiPointsByNeighborhood",
        fieldType = ListType(WifiPoint.Type),
        arguments = List(
          Argument("neighborhood", StringType),
          Argument("limit", IntType),
          Argument("offset", IntType)
        ),
        resolve = ctx => {
          val neighborhood = ctx.arg[String]("neighborhood")
          val limit        = ctx.arg[Int]("limit")
          val offset       = ctx.arg[Int]("offset")
          service.getWifiPointsByNeighborhood(neighborhood, limit, offset).unsafeToFuture()
        }
      ),
      Field(
        name = "wifiPointsByProximity",
        fieldType = ListType(WifiPoint.Type),
        arguments = List(
          Argument("latitude", FloatType),
          Argument("longitude", FloatType),
          Argument("distance", FloatType)
        ),
        resolve = ctx => {
          val latitude  = ctx.arg[Double]("latitude")
          val longitude = ctx.arg[Double]("longitude")
          val distance  = ctx.arg[Double]("distance")
          service.getWifiPointsByProximity(latitude, longitude, distance).unsafeToFuture()
        }
      )
    )
  )
}
