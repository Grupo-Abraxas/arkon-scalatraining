package domain

import sangria.macros.derive.GraphQLField

case class Activity(
                     @GraphQLField id: Int,
                     @GraphQLField name: String,
                   )
