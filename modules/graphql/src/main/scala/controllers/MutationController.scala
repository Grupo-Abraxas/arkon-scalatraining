package controllers

import models.WifiPoint
import sangria.schema._
import services.WifiPointService

class MutationController(service: WifiPointService) {
  val MutationType: ObjectType[Unit, Unit] = ObjectType(
    "Mutation",
    fields[Unit, Unit](
      Field(
        name = "addWifiPoint",
        fieldType = WifiPoint.Type,
        arguments = WifiPoint.inputArguments,
        resolve = ctx =>
          service.addWifiPoint(
            id = ctx.arg[String]("id"),
            program = ctx.arg[String]("program"),
            installationDate = ctx.argOpt[String]("installationDate"),
            latitude = ctx.arg[Double]("latitude"),
            longitude = ctx.arg[Double]("longitude"),
            neighborhood = ctx.arg[String]("neighborhood"),
            municipality = ctx.arg[String]("municipality")
          )
      ),
      Field(
        name = "deleteWifiPoint",
        fieldType = StringType,
        arguments = List(Argument("id", StringType)),
        resolve = ctx => service.deleteWifiPoint(ctx.arg[String]("id"))
      )
    )
  )
}
