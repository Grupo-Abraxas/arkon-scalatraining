package graphql

import model.Estado
import repository.EstadoRepo
import sangria.schema.Args.empty.arg
import sangria.schema._
object SangriaGraphql {

  val EstadoType = ObjectType(
    "Estado",
    "descripcion",
    fields[Unit, Estado](
      Field("id", IntType, resolve = _.value.id),
      Field("Description", StringType, resolve = _.value.Description)
    )
  )

  val IdEstatus = Argument("id", IntType)

  val QueryType  = ObjectType[EstadoRepo, Unit](
    "Query",
    "consultas",
    fields[EstadoRepo, Unit](
      Field( "estado", EstadoType,
        description = Some("Entrega un estatus x id"),
        arguments = IdEstatus :: Nil,
        resolve = ctx => ctx.ctx.findEstadoById(ctx.arg(IdEstatus))
      ),
      Field( "estados", ListType(EstadoType),
        description = Some("Entrega el listado de estatus existentes"),
        resolve = c => c.ctx.findAllEstado()
      )
    )
  )
  val estadoSchema = Schema(QueryType)
}


