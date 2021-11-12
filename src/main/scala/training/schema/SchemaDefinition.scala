package training.schema

import sangria.schema.{fields, ObjectType, Schema}

import scala.language.postfixOps

object SchemaDefinition {
  val Query: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
    )
  )

  val AppSchema: Schema[Unit, Unit] = Schema(Query)
}
