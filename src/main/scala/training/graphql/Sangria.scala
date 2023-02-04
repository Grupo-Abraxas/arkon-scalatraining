package training.graphql

import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global

import sangria.schema._
import sangria.macros._

import training.models.{Shop, Product}
import training.repository.{Repo}

object Sangria {
    val ShopType = ObjectType("Shop", fields[Unit, Shop](
      Field("id", LongType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("business_name", OptionType(StringType), resolve = _.value.business_name),
      Field("activity", StringType, resolve = _.value.activity),
      Field("stratum", StringType, resolve = _.value.stratum),
      Field("address", StringType, resolve = _.value.address),
      Field("phone_number", OptionType(StringType), resolve = _.value.phone_number),
      Field("email", OptionType(StringType), resolve = _.value.email),
      Field("website", OptionType(StringType), resolve = _.value.website),
      Field("shop_type", StringType, resolve = _.value.shop_type),
      Field("longitude", FloatType, resolve = _.value.longitude),
      Field("latitude", FloatType, resolve = _.value.latitude)
    ))

    val Id = Argument("id", LongType)

    val QueryType = ObjectType("Query", fields[Repo, Unit](
      Field("shop", OptionType(ShopType),
        description = Some("Returns a Shop with specific `id`."),
        arguments = Id :: Nil,
        resolve = c => c.ctx.shop(c arg Id).unsafeToFuture)))

    //   Field("products", ListType(ProductType),
    //     description = Some("Returns a list of all available products."),
    //     resolve = _.ctx.products.unsafeToFuture)))

    // val nameArg = Argument("name", StringType)
    // val descriptionArg = Argument("description", StringType)

    // val MutationType = ObjectType("Mutation", fields[Repo, Unit](
    //   Field("addProduct", ProductType,
    //     arguments = nameArg :: descriptionArg :: Nil,
    //     resolve = c => c.ctx.addProduct(c.arg(nameArg), c.arg(descriptionArg)).unsafeToFuture)))

    // val schema = Schema(QueryType, Some(MutationType)
    val schema = Schema(QueryType)
  }