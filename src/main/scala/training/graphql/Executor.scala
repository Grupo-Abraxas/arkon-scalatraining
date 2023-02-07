package training.graphql

import cats.effect._

import scala.util.{Failure, Success, Try}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import sangria.execution.{ Executor => Ex }
import sangria.marshalling.circe._

import org.http4s.circe._
import org.http4s.dsl.io._

import doobie.hikari.HikariTransactor

import training.models.{RequestJson}
import training.graphql.Sangria.{schema}
import training.repository.{Repo}
import training.graphql.Parser

/** Respuesta genérica de error.
 *
 *  @constructor crea la respuesta de error
 *  @param error mensaje de error
 */
case class ErrorResponse(error: String)

/** Fabrica para instancias de [[training.graphql.Executor]]. */
object Executor {
    /** Usa la definición de sangria para ejecutar un query
     *
     *  @param db transactor de base de datos
     *  @param requestJson petición en formato JSON
     *  @return IO.
     */
    def execute(db: HikariTransactor[IO], requestJson: RequestJson) = {
        val qs = requestJson.query.stripMargin
        
        Parser.parse(qs) match {
            case Right(document) => {
                val result: Future[Json] = Ex.execute(schema, document,  new Repo(db))
                Try(Await.result(result, 10 seconds)) match {
                    case Success(result) => Ok(result)
                    case Failure(error) => responseWithError(error.getMessage)
                }
            }
            case Left(error) => responseWithError(error.getMessage)
        }
    }

    /** Regresa una respuesta con error
     *
     *  @param message mensaje de error
     *  @return IO.
     */
    def responseWithError(message: String) = BadRequest(ErrorResponse(message).asJson)
}
