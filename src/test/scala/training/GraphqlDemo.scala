package training

import graphql.SangriaGraphql.SchemaEstado
import io.circe.Json
import org.http4s.Status.Ok
import repository.EstadoRepo
import sangria.execution.Executor
import sangria.macros.LiteralGraphQLStringContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object GraphqlDemo  extends App{

  val schema = SchemaEstado;

  val query = graphql"""
        { estado(id:1) {
          id
          description
        }
        }
      """
  //ListMap(data -> ListMap(estados -> Vector(ListMap(id -> 1, description -> Activo), ListMap(id -> 2, description -> Inactivo))))
    val resultado  =Executor.execute( schema,query)

     Await.result(resultado.map(Ok -> println(_)), 10 second)



}
