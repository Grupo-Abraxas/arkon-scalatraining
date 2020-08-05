package training.std

import sangria.execution.deferred.{DeferredResolver, Fetcher, Relation}
import sangria.macros.derive._
import sangria.schema._
import training.std.Models._

object SchemaDefinition {

  val shopCaRel = Relation[Shop, Int]("activity_id", v => Seq(v.activity_id))
  val caShopRel = Relation[ComercialActivity, Int]("id", v => Seq(v.id))
  val shopTypeRel = Relation[ShopType, Int]("id", v => Seq(v.id))
  val stratumRel = Relation[Stratum, Int]("id", v => Seq(v.id))
  //Relation fetchers
  val caFetcher = Fetcher((ctx: ShopRepository, ids: Seq[Int]) => ctx.activities(ids))
  val shopTypeFetcher = Fetcher((ctx: ShopRepository, ids: Seq[Int]) => ctx.shopTypes(ids))
  val stratumFetcher = Fetcher((ctx: ShopRepository, ids: Seq[Int]) => ctx.stratums(ids))

  //Resolver fetchers
  val Resolver = DeferredResolver.fetchers(caFetcher, shopTypeFetcher, stratumFetcher)

  //GraphQl Types
  implicit val shopType: ObjectType[Unit, Shop] = {
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
          resolve = c => stratumFetcher.defer(c.value.stratum_id)),
        Field("nearbyShops",
          ListType(shopType),
          resolve = c => new ShopRepository().nearByShop(c.value.id)),
        Field("shopsInRadius",
          ListType(shopType),
          resolve = c => new ShopRepository().radiusByShop(c.value.id))
      )
    )
  }
  implicit val activityType: ObjectType[Unit, ComercialActivity] =
    deriveObjectType[Unit, ComercialActivity]()
  implicit val shopTypeType: ObjectType[Unit, ShopType] =
    deriveObjectType[Unit, ShopType]()
  implicit val stratumType: ObjectType[Unit, Stratum] =
    deriveObjectType[Unit, Stratum]()
  implicit val payloadType: ObjectType[Unit, CreateShopPayload] =
    deriveObjectType[Unit, CreateShopPayload]()
}
