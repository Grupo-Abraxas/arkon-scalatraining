package domain

import sangria.macros.derive.GraphQLField

case class ShopType(
                     @GraphQLField id: Int,
                     @GraphQLField name: String,
                   )
