package schemas

import Repo.ProductRepo
import sangria.schema._

object QueryType {
  val Id = Argument("id", StringType)

  val QueryType = ObjectType("Query", fields[ProductRepo, Unit](
    Field("product", OptionType(ProductType.ProductType),
      description = Some("Returns a product with specific `id`."),
      arguments = Id :: Nil,
      resolve = c => c.ctx.product(c arg Id)),

    Field("products", ListType(ProductType.ProductType),
      description = Some("Returns a list of all available products."),
      resolve = _.ctx.products)))

  val schema = Schema(QueryType)

}
