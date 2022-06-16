package schemas

import sangria.schema._
import sangria.macros.derive._
import models.Product

object ProductType {
  import PictureType._

  val ProductType =
    deriveObjectType[Unit, Product](
      Interfaces(IdentifiableType),
      IncludeMethods("picture"))

  val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity that can be identified",

    fields[Unit, Identifiable](
      Field("id", StringType, resolve = _.value.id)))
}
