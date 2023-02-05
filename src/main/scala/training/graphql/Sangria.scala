package training.graphql

import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global

import sangria.schema._
import sangria.macros._
import sangria.marshalling.circe._

import io.circe.generic.auto._
import io.circe.syntax._

import training.models.{Shop, ShopInput}
import training.repository.{Repo}

object Sangria {
    val ShopType = ObjectType("Shop", fields[Unit, Shop](
      Field("id", LongType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("businessName", OptionType(StringType), resolve = _.value.businessName),
      Field("activity", StringType, resolve = _.value.activity),
      Field("stratum", StringType, resolve = _.value.stratum),
      Field("address", StringType, resolve = _.value.address),
      Field("phoneNumber", OptionType(StringType), resolve = _.value.phoneNumber),
      Field("email", OptionType(StringType), resolve = _.value.email),
      Field("website", OptionType(StringType), resolve = _.value.website),
      Field("shopType", StringType, resolve = _.value.shopType),
      Field("longitude", FloatType, resolve = _.value.longitude),
      Field("latitude", FloatType, resolve = _.value.latitude)
    ))

    val Id = Argument("id", LongType)
    val Limit = Argument("limit", IntType)
    val Offset = Argument("offset", IntType)
    val Radius = Argument("radius", IntType)
    val Latitude = Argument("lat", FloatType)
    val Longitude = Argument("lng", FloatType)

    val QueryType = ObjectType("Query", fields[Repo, Unit](
      Field("shop", OptionType(ShopType),
        description = Some("Returns a Shop with specific `id`."),
        arguments = Id :: Nil,
        resolve = c => c.ctx.shop(c arg Id).unsafeToFuture),

      Field("shops", ListType(ShopType),
        arguments = Limit :: Offset :: Nil,
        description = Some("Returns a list of all available shops."),
        resolve = c => c.ctx.listShops(c.arg(Limit), c.arg(Offset)).unsafeToFuture),

      Field("nearbyShops", ListType(ShopType),
        arguments = Limit :: Latitude :: Longitude :: Nil,
        description = Some("Returns a list of all available shops."),
        resolve = c => c.ctx.nearbyShops(c.arg(Limit), c.arg(Latitude), c.arg(Longitude)).unsafeToFuture),

      Field("shopsInRadius", ListType(ShopType),
        arguments = Radius :: Latitude :: Longitude :: Nil,
        description = Some("Returns a list of all available shops."),
        resolve = c => c.ctx.shopsInRadius(c.arg(Radius), c.arg(Latitude), c.arg(Longitude)).unsafeToFuture)))
    
    val CreateShopInput = InputObjectType[ShopInput]("ShopInput", List(
      InputField("name", StringType),
      InputField("bussinesName", OptionInputType(StringType)),
      InputField("activity", StringType),
      InputField("stratum", StringType),
      InputField("address", StringType),
      InputField("phoneNumber", OptionInputType(StringType)),
      InputField("email", OptionInputType(StringType)),
      InputField("website", OptionInputType(StringType)),
      InputField("shopType", StringType),
      InputField("longitude", FloatType),
      InputField("latitude", FloatType)))
      
    val ShopInput = Argument("input", CreateShopInput)

    val MutationType = ObjectType("Mutation", fields[Repo, Unit](
      Field("createShop", ShopType,
        arguments = ShopInput :: Nil,
        resolve = c => c.ctx.insertShop(c arg ShopInput).unsafeToFuture)))

    val schema = Schema(QueryType, Some(MutationType))
  }