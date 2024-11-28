package controllers

import sangria.schema._

class SchemaDefinition(
    queryController: QueryController,
    mutationController: MutationController
) {
  val schema: Schema[Unit, Unit] = Schema(
    query = queryController.QueryType,
    mutation = Some(mutationController.MutationType)
  )
}
