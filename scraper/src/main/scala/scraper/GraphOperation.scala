package scraper

import sangria.macros.derive.deriveObjectType
import sangria.schema.{Argument, Field, IntType, ObjectType, OptionInputType, Schema, StringType, fields}
import common.Models.Response

object GraphOperation {
  //Queries Argument Types
  val businessType = Argument("businessType", StringType)
  val lat = Argument("lat", StringType)
  val long = Argument("long", StringType)
  val radius = Argument("radius", OptionInputType(IntType), "", 5000)

  implicit val responseType: ObjectType[Unit, Response] =
    deriveObjectType[Unit, Response]()

  //GraphQl queries
  val ScraperType = ObjectType("Query", fields[ScraperRepository, Unit](
    Field("scraper",
      responseType,
      arguments = businessType :: lat :: long :: radius :: Nil,
      resolve = c => c.ctx.scrapeData(c.arg(businessType), c.arg(lat), c.arg(long), c.arg(radius))
    )
  )
  )

  //GraphQl schema
  val ScraperSchema = Schema(ScraperType)
}
