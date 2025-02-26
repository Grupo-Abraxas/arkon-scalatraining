package controllers

import models.WifiPoint
import sangria.schema._
import services.WifiPointService
import cats.effect.unsafe.implicits.global

class MutationController(service: WifiPointService) {
  val MutationType: ObjectType[Unit, Unit] = ObjectType(
    "Mutation",
    fields[Unit, Unit](
      Field(
        name = "addWifiPoint",
        fieldType = WifiPoint.Type,
        arguments = List(
          Argument("id", OptionInputType(IntType)),
          Argument("program", StringType),
          Argument("installationDate", OptionInputType(StringType)),
          Argument("latitude", FloatType),
          Argument("longitude", FloatType),
          Argument("neighborhood", StringType),
          Argument("municipality", StringType)
        ),
        resolve = ctx =>
          service.addWifiPoint(
            id = ctx.argOpt[Int]("id"),
            program = ctx.arg[String]("program"),
            installationDate = ctx.argOpt[String]("installationDate"),
            latitude = ctx.arg[Double]("latitude"),
            longitude = ctx.arg[Double]("longitude"),
            neighborhood = ctx.arg[String]("neighborhood"),
            municipality = ctx.arg[String]("municipality")
          ).unsafeToFuture()
      ),
      Field(
        name = "deleteWifiPoint",
        fieldType = StringType,
        arguments = List(Argument("id", IntType)),
        resolve = ctx => service.deleteWifiPoint(ctx.arg[Int]("id")).unsafeToFuture()
      )
    )
  )
}
