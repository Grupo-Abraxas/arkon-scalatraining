package training.std

import sangria.schema.{Argument, Field, FloatType, IntType, ListType, ObjectType, OptionInputType, OptionType, Schema, StringType, fields}
import common.Models.CreateShopInput
import training.std.SchemaDefinition.{payloadType, shopType}

object GraphOperation {
  //Queries Argument Types
  val id = Argument("id", IntType)
  val limit = Argument("limit", OptionInputType(IntType), "", 5)
  val offset = Argument("offset", IntType)
  val radius = Argument("radius", OptionInputType(IntType), "", 50)

  //Mutation Argument Types
  val name = Argument("name", StringType)
  val businessName = Argument("businessName", StringType)
  val activity = Argument("activity", StringType)
  val stratum = Argument("stratum", StringType)
  val address = Argument("address", StringType)
  val phone = Argument("phoneNumber", StringType)
  val email = Argument("email", StringType)
  val web = Argument("website", StringType)
  val shopT = Argument("shopType", StringType)
  val lat = Argument("lat", FloatType)
  val long = Argument("long", FloatType)

  //GraphQl queries
  val QueryType = ObjectType("Query", fields[ShopRepository, Unit](
    Field("shop",
      OptionType(shopType),
      arguments = id :: Nil,
      resolve = c => c.ctx.shop(c.arg(id))
    ),
    Field("shops",
      ListType(shopType),
      arguments = limit :: offset :: Nil,
      resolve = c => c.ctx.shops(c.arg(limit), c.arg(offset))
    ),
    Field("nearbyShops",
      ListType(shopType),
      arguments = limit :: lat :: long :: Nil,
      resolve = c => c.ctx.nearbyShops(c.arg(limit), c.arg(lat).toFloat, c.arg(long).toFloat)
    ),
    Field("shopsInRadius",
      ListType(shopType),
      arguments = radius :: lat :: long :: Nil,
      resolve = c => c.ctx.shopsInRadius(c.arg(radius), c.arg(lat).toFloat, c.arg(long).toFloat)
    )
  )
  )

  //GraphQl mutation
  val Mutation = ObjectType("Mutation", fields[ShopRepository, Unit](
    Field("createShop", payloadType,
      arguments = id :: name :: businessName :: activity :: stratum :: address :: phone ::
        email :: web :: shopT :: lat :: long :: Nil,
      resolve = c => c.ctx.createShop(CreateShopInput(c.arg(id), c.arg(name), c.arg(businessName), c.arg(activity), c.arg(stratum),
        c.arg(address), c.arg(phone), c.arg(email), c.arg(web), c.arg(shopT), c.arg(lat).toFloat, c.arg(long).toFloat))
    )
  ))

  //GraphQl schema
  val ShopSchema = Schema(QueryType, Some(Mutation))
}
