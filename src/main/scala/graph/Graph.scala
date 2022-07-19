package training.graph

import scala.concurrent.{ Future }
import scala.util.{ Failure, Success }

import cats.effect._
import cats.implicits._
import io.circe.{ Json, JsonObject }
import io.circe.optics.JsonPath.root
import sangria.ast.{ Document => AstDocument }
import sangria.execution.{ Executor, WithViolations }
import sangria.execution.deferred.{ DeferredResolver }
import sangria.macros.derive._
import sangria.marshalling.circe._
import sangria.parser.{ QueryParser, SyntaxError }
import sangria.schema._
import sangria.validation.AstNodeViolation

import training.database.{ Manager }
import training.model.Activity
import sangria.marshalling.queryAst
import sangria.execution.ExceptionHandler
import sangria.execution.HandledException
import scala.concurrent.ExecutionContext

trait Graph[F[_]] {

  def parse(request: Json): F[Either[Json, Json]]

  def parse(query: String, operationName: Option[String], variables: JsonObject): F[Either[Json, Json]]

}

object Graph {

  final class Partial[F[_]] {

    def apply[A](
      schema: Schema[A, Unit],
      userContext: F[A],
      executionContext: ExecutionContext
    )(implicit
      F: Async[F]
    ) =
      new Graph[F] {

        def parse(request: Json): F[Either[Json, Json]] = {
          val query         = root.query.string getOption request
          val operationName = root.operationName.string getOption request
          val variables     = root.variables.obj getOption request getOrElse (JsonObject())

          query match {
            case Some(query) => parse(query, operationName, variables)
            case None        => F.pure(formatStringError("Missing `query` property.").asLeft)
          }
        }

        def parse(query: String, operationName: Option[String], variables: JsonObject): F[Either[Json, Json]] =
          QueryParser.parse(query) match {
            case Success(ast)                                   => execute(ast, operationName, variables)(executionContext)
            case Failure(error @ SyntaxError(_, _, parseError)) => F.pure(formatSyntaxError(error).asLeft)
            case Failure(error)                                 => F.pure(formatThrowableError(error).asLeft)
          }

        def execute(query: AstDocument, operationName: Option[String], variables: JsonObject)(implicit
          ec: ExecutionContext
        ): F[Either[Json, Json]] =
          for {
            context <- userContext
            attempt <- F.async_ { (cb: Either[Throwable, Json] => Unit) =>
                         Executor
                           .execute(
                             schema           = schema,
                             queryAst         = query,
                             userContext      = context,
                             operationName    = operationName,
                             variables        = Json.fromJsonObject(variables),
                             exceptionHandler = ExceptionHandler { case (_, e) => HandledException(e.getMessage) }
                           )
                           .onComplete {
                             case Success(value) => cb(Right(value))
                             case Failure(error) => cb(Left(error))
                           }
                       }.attempt
            result <- attempt match {
                        case Right(json)                 => F.pure(json.asRight)
                        case Left(error: WithViolations) => F.pure(formatWithViolationsError(error).asLeft)
                        case Left(error)                 => F.pure(formatThrowableError(error).asLeft)
                      }
          } yield result
      }
  }

  def apply[F[_]] = new Partial[F]

  def formatStringError(error: String): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "message" -> Json.fromString(error)
        )
      )
    )

  def formatThrowableError(error: Throwable): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "class"   -> Json.fromString(error.getClass.getName),
          "message" -> Json.fromString(error.getMessage)
        )
      )
    )

  def formatSyntaxError(error: SyntaxError): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "message" -> Json.fromString(error.getMessage),
          "locations" -> Json.arr(
            Json.obj(
              "line"   -> Json.fromInt(error.originalError.position.line),
              "column" -> Json.fromInt(error.originalError.position.column)
            )
          )
        )
      )
    )

  def formatWithViolationsError(error: WithViolations): Json =
    Json.obj(
      "errors" -> Json.fromValues(
        error.violations map {
          case v: AstNodeViolation =>
            Json.obj(
              "message" -> Json.fromString(v.errorMessage),
              "locations" -> Json.fromValues(
                v.locations map (l => Json.obj("line" -> Json.fromInt(l.line), "column" -> Json.fromInt(l.column)))
              )
            )
          case v =>
            Json.obj(
              "message" -> Json.fromString(v.errorMessage)
            )
        }
      )
    )

}
