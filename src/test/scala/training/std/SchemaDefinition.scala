package training.std

import sangria.execution.deferred.{DeferredResolver, Fetcher, Relation, RelationIds}
import sangria.macros.derive._
import sangria.schema._
import training.std.Models._

object SchemaDefinition {

  //GraphQl Types
  implicit val shopType: ObjectType[Unit, Shop] =
    deriveObjectType[Unit, Shop](
      ExcludeFields("activity_id"),
      ExcludeFields("shop_type_id"),
      ExcludeFields("stratum_id"),
      AddFields(
        Field("activity",
          activityType,
          resolve = c => caFetcher.defer(c.value.activity_id)),
        Field("shopType",
          shopTypeType,
          resolve = c => shopTypeFetcher.defer(c.value.shop_type_id)),
        Field("stratum",
          stratumType,
          resolve = c => stratumFetcher.defer(c.value.stratum_id))
      )
    )

  implicit val activityType: ObjectType[Unit, ComercialActivity] =
    deriveObjectType[Unit, ComercialActivity](
      ObjectTypeDescription("The activities type"),
      DocumentField("id", "Number id of activity"),
      DocumentField("name", "Name of comercial activity")
    )

  implicit val shopTypeType: ObjectType[Unit, ShopType] =
    deriveObjectType[Unit, ShopType]()

  implicit val stratumType: ObjectType[Unit, Stratum] =
    deriveObjectType[Unit, Stratum]()

  //Table Relations
  val shopCaRel = Relation[Shop, Int]("activity_id", v => Seq(v.activity_id))
  val caShopRel = Relation[ComercialActivity, Int]("id", v => Seq(v.id))
  val shopTypeRel = Relation[ShopType, Int]("id", v => Seq(v.id))
  val stratumRel = Relation[Stratum, Int]("id", v => Seq(v.id))

  //Relation fetchers
  val shopFetcher = Fetcher.rel(
    (ctx: ShopRepository, ids: Seq[Int]) => ctx.shops(ids),
    (ctx: ShopRepository, ids: RelationIds[Shop]) => ctx.shops(ids(shopCaRel))
  )
  val caFetcher = Fetcher.rel(
    (ctx: ShopRepository, ids: Seq[Int]) => ctx.activities(ids),
    (ctx: ShopRepository, ids: RelationIds[ComercialActivity]) => ctx.activityByRelShopIds(ids(caShopRel))
  )
  val shopTypeFetcher = Fetcher.rel(
    (ctx: ShopRepository, ids: Seq[Int]) => ctx.shopTypes(ids),
    (ctx: ShopRepository, ids: RelationIds[ShopType]) => ctx.shopTypeByRelShopIds(ids(shopTypeRel))
  )
  val stratumFetcher = Fetcher.rel(
    (ctx: ShopRepository, ids: Seq[Int]) => ctx.stratums(ids),
    (ctx: ShopRepository, ids: RelationIds[Stratum]) => ctx.stratumsByRelShopIds(ids(stratumRel))
  )

  //Resolver fetchers
  val Resolver = DeferredResolver.fetchers(shopFetcher, caFetcher, shopTypeFetcher, stratumFetcher)

  //Argument Types
  val id = Argument("id", IntType)
  val ids = Argument("ids", ListInputType(IntType))

  //GraphQl queries
  val QueryType = ObjectType("Query", fields[ShopRepository, Unit](
      Field("shop",
        OptionType(shopType),
        arguments = id :: Nil,
        resolve = c => shopFetcher.deferOpt(c.arg(id))
      ),
      Field("shops",
        ListType(shopType),
        arguments = List(ids),
        resolve = c => shopFetcher.deferSeq(c.arg(ids))
      )
    )
  )

  /*mutation {
  createShop(
    id: 5,
    name: "",
    businessName: "",
    activity: 1,
    stratum: 2,
    address: "",
    phoneNumber: "5585858585",
    email: "@mail.com",
    website: "www.host.com",
    shopType: 1,
    position: "aes68as6878as68as7d68")
}
*/

  val IdArg = Argument("id", IntType)
  val NameArg = Argument("name", StringType)
  val businessNameArg = Argument("businessName", StringType)
  val activityArg = Argument("activity", IntType)
  val stratumArg = Argument("stratum", IntType)
  val addressArg = Argument("address", StringType)
  val phoneArg = Argument("phoneNumber", StringType)
  val emailArg = Argument("email", StringType)
  val webArg = Argument("website", StringType)
  val shopTypeArg = Argument("shopType", IntType)
  val positionArg = Argument("position", StringType)
  //@TODO Check position type treatment
  val Mutation = ObjectType("Mutation", fields[ShopRepository, Unit](
    Field("createShop", shopType,
      arguments = IdArg :: NameArg :: businessNameArg :: activityArg :: stratumArg :: addressArg :: phoneArg ::
        emailArg :: webArg :: shopTypeArg :: positionArg :: Nil,
      resolve = c => c.ctx.addShop(
        c.arg(IdArg), c.arg(NameArg), c.arg(businessNameArg), c.arg(activityArg), c.arg(stratumArg),
        c.arg(addressArg), c.arg(phoneArg), c.arg(emailArg), c.arg(webArg), c.arg(shopTypeArg), c.arg(positionArg)
      )
    )
  ))

  val ShopSchema = Schema(QueryType, Some(Mutation))

}
