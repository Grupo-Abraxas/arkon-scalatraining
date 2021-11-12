package domain

import sangria.macros.derive.GraphQLField

case class Stratum(
                    @GraphQLField id: Int,
                    @GraphQLField name: String,
                  )
