package domain

import sangria.macros.derive.GraphQLField

case class Shop(
                 @GraphQLField id: Int,
                 @GraphQLField name: String,
                 @GraphQLField business_name: String,
                 activity_id: Int,
                 stratum_id: Int,
                 @GraphQLField address: String,
                 @GraphQLField phone_number: String,
                 @GraphQLField email: String,
                 @GraphQLField website: String,
                 shop_type_id: Int,
//                 @GraphQLField lat: Float,
//                 @GraphQLField long: Float,
               )
