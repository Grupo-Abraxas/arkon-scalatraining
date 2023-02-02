package training.graphql

import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global

import sangria.schema._
import sangria.macros._

import training.models.{Product}
import training.repository.{Repo}

object Sangria {
    val ProductType = ObjectType("Product", fields[Unit, Product](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("description", StringType, resolve = _.value.description)
    ))

    val Id = Argument("id", IntType)

    val QueryType = ObjectType("Query", fields[Repo, Unit](
      Field("product", OptionType(ProductType),
        description = Some("Returns a product with specific `id`."),
        arguments = Id :: Nil,
        resolve = c => c.ctx.product(c arg Id).unsafeToFuture),

      Field("products", ListType(ProductType),
        description = Some("Returns a list of all available products."),
        resolve = _.ctx.products.unsafeToFuture)))

    val nameArg = Argument("name", StringType)
    val descriptionArg = Argument("description", StringType)

    val MutationType = ObjectType("Mutation", fields[Repo, Unit](
      Field("addProduct", ProductType,
        arguments = nameArg :: descriptionArg :: Nil,
        resolve = c => c.ctx.addProduct(c.arg(nameArg), c.arg(descriptionArg)).unsafeToFuture)))

    val schema = Schema(QueryType, Some(MutationType))
}