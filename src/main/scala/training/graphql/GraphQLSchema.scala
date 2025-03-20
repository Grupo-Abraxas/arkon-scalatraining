package training.graphql

import sangria.schema._
import sangria.macros.derive._
import training.models.{Shop, ShopType, Stratum, Activity}
import training.controllers.ShopController
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.postgresql.geometric.PGpoint
import sangria.marshalling.{FromInput, CoercedScalaResultMarshaller}
import sangria.validation.ValueCoercionViolation

object GraphQLSchema {
  
  case object InvalidPGpointFormat extends ValueCoercionViolation("Invalid PGpoint format")
  
  implicit val ShopTypeType: ObjectType[Unit, ShopType] = deriveObjectType[Unit, ShopType]()
  implicit val StratumType: ObjectType[Unit, Stratum] = deriveObjectType[Unit, Stratum]()
  implicit val ActivityType: ObjectType[Unit, Activity] = deriveObjectType[Unit, Activity]()
  
  implicit val PGpointType: ScalarType[PGpoint] = ScalarType[PGpoint](
    "PGpoint",
    description = Some("A PostgreSQL geometric point"),
    coerceOutput = (value, _) => s"(${value.x}, ${value.y})",
    coerceUserInput = {
      case s: String =>
        try {
          val coords = s.stripPrefix("(").stripSuffix(")").split(",").map(_.trim.toDouble)
          Right(new PGpoint(coords(0), coords(1)))
        } catch {
          case _: Exception => Left(InvalidPGpointFormat)
        }
      case _ => Left(InvalidPGpointFormat)
    },
    coerceInput = {
      case sangria.ast.StringValue(s, _, _, _, _) =>
        try {
          val coords = s.stripPrefix("(").stripSuffix(")").split(",").map(_.trim.toDouble)
          Right(new PGpoint(coords(0), coords(1)))
        } catch {
          case _: Exception => Left(InvalidPGpointFormat)
        }
      case _ => Left(InvalidPGpointFormat)
    }
  )
  
  implicit val ShopType: ObjectType[Unit, Shop] = deriveObjectType[Unit, Shop](
    ReplaceField("id", Field("id", IntType, resolve = _.value.id))
  )
  
  implicit val shopFromInput: FromInput[Shop] = new FromInput[Shop] {
    val marshaller = CoercedScalaResultMarshaller.default
    
    def fromResult(node: marshaller.Node): Shop = {
      val map = node.asInstanceOf[Map[String, Any]]

      Shop(
        id = map.getOrElse("id", 0).asInstanceOf[Int],
        name = map("name").asInstanceOf[String],
        businessName = map.get("businessName").flatMap(_.asInstanceOf[Option[String]]),
        activityId = map("activityId").asInstanceOf[Int],
        stratumId = map("stratumId").asInstanceOf[Int],
        address = map("address").asInstanceOf[String],
        phoneNumber = map.get("phoneNumber").flatMap(_.asInstanceOf[Option[String]]),
        email = map.get("email").flatMap(_.asInstanceOf[Option[String]]),
        website = map.get("website").flatMap(_.asInstanceOf[Option[String]]),
        shopTypeId = map("shopTypeId").asInstanceOf[Int],
        position = map.get("position").flatMap(_.asInstanceOf[Option[PGpoint]]).orNull
      )
    }
  }
  
  implicit val ShopInputType: InputObjectType[Shop] = InputObjectType[Shop](
    "ShopInput",
    fields = List(
      InputField("name", StringType),
      InputField("businessName", OptionInputType(StringType)),
      InputField("activityId", IntType),
      InputField("stratumId", IntType),
      InputField("address", StringType),
      InputField("phoneNumber", OptionInputType(StringType)),
      InputField("email", OptionInputType(StringType)),
      InputField("website", OptionInputType(StringType)),
      InputField("shopTypeId", IntType),
      InputField("position", OptionInputType(PGpointType))
    )
  )
  
  val IdArg = Argument("id", IntType)
  val LimitArg = Argument("limit", IntType, defaultValue = 10)
  val OffsetArg = Argument("offset", IntType, defaultValue = 0)
  val LatArg = Argument("lat", FloatType)
  val LongArg = Argument("long", FloatType)
  val RadiusArg = Argument("radius", IntType, defaultValue = 1000)
  
  val ShopInputArg = Argument("shop", ShopInputType)
  
  val QueryType = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field("shop", OptionType(ShopType),
        arguments = IdArg :: Nil,
        resolve = c => ShopController.getShop(c.arg(IdArg)).unsafeRunSync()
      ),
      Field("shops", ListType(ShopType),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = c => ShopController.getShops(c.arg(LimitArg), c.arg(OffsetArg)).unsafeRunSync()
      ),
      Field("nearbyShops", ListType(ShopType),
        arguments = LatArg :: LongArg :: RadiusArg :: LimitArg :: Nil,
        resolve = c => ShopController.getNearbyShops(
          c.arg(LatArg).toFloat,
          c.arg(LongArg).toFloat, 
          c.arg(RadiusArg),
          c.arg(LimitArg)
        ).unsafeRunSync()
      )
    )
  )
  
  val MutationType = ObjectType(
    "Mutation",
    fields[Unit, Unit](
      Field("createShop", IntType,
        arguments = ShopInputArg :: Nil,
        resolve = c => ShopController.createShop(c.arg(ShopInputArg)).unsafeRunSync()
      )
    )
  )
  
  val schema = Schema(QueryType, Some(MutationType))
}