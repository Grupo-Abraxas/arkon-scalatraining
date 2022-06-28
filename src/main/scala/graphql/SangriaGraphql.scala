package graphql

import model.Estatus
import repository.EstatusRepo
import sangria.schema._
object SangriaGraphql {

  val EstatusType = ObjectType(
    "Estatus",
    "descripcion",
    fields[Unit, Estatus](
      Field("id", IntType, resolve = _.value.id),
      Field("Description", StringType, resolve = _.value.Description)
    )
  )

  val IdEstatus = Argument("id", IntType)

  val QueryType  = ObjectType(
    "Query",
    "consultas",
    fields[EstatusRepo, Unit](
      Field(
        "estatus",
        OptionType(EstatusType),
        description = Some("Entrega un estatus x id"),
        arguments = IdEstatus :: Nil,
        resolve = c => c.ctx.findEstatusById(c arg IdEstatus)
      ),
      Field(
        "estatusList",
        ListType(EstatusType),
        description = Some("Entrega el listado de estatus existentes"),
        resolve = c => c.ctx.findAllEstatus()
      )
    )
  )

}
