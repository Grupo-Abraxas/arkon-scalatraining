package training

import graphql.SangriaGraphql.SchemaEstado
import io.circe.{Json, JsonObject}
import repository.EstadoRepo
import sangria.execution.{ErrorWithResolver, ExecutionResult, Executor, QueryAnalysisError}

import sangria.macros.LiteralGraphQLStringContext

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

import sangria.marshalling.circe._

object GraphqlDemo  extends App{

  val schema = SchemaEstado;

  val query = graphql"""
       {

          estados {
            description
          }
        }
      """
  //ListMap(data -> ListMap(estados -> Vector(ListMap(id -> 1, description -> Activo), ListMap(id -> 2, description -> Inactivo))))
    /*val resultado  =Executor.execute( schema,query)

     Await.result(resultado.map(Ok -> println(_)), 10 second)*/
    val resultado: Future[Json]  = Executor.execute( schema,query, new EstadoRepo  )

  Try(Await.result(resultado, 10 seconds)) match {
    case Success(extractedVal) =>  {
      println(extractedVal)}
    case Failure(_) => { println("Failure Happened") }
    case _ => { println("Very Strange") }
  }

}
