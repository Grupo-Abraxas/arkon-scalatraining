package com.arkondata.training

import scala.concurrent.ExecutionContext

import cats.data.{EitherT, Kleisli}
import cats.effect.Async
import cats.syntax.applicativeError.catsSyntaxApplicativeError
import cats.syntax.either.catsSyntaxEither
import cats.syntax.flatMap.toFlatMapOps
import io.circe.{DecodingFailure, Json}
import sangria.ast.Document
import sangria.execution.{Executor, WithViolations}
import sangria.marshalling.circe.{CirceInputUnmarshaller, CirceResultMarshaller}
import sangria.parser.{QueryParser, SyntaxError}
import sangria.schema.Schema

trait GraphQL[F[_]]:

  def query: Kleisli[EitherT[F, Json, *], Json, Json]

end GraphQL

object GraphQL:

  def apply[F[_]: Async, A](
      schema: Schema[A, Unit],
      userContext: F[A],
      executionContext: ExecutionContext
  ): GraphQL[F] =
    new GraphQL[F]:

      def decodeQuery: Kleisli[EitherT[F, Json, *], Json, (String, Option[String], Json)] =
        Kleisli(json =>
          EitherT
            .fromEither(json.hcursor.downField("query").as[String].leftMap(decodingFailureToJson(_)))
            .map(query =>
              (
                query,
                json.hcursor.downField("operationName").as[String].toOption,
                json.hcursor.downField("variables").focus.getOrElse(Json.obj())
              )
            )
        )

      def parse: Kleisli[EitherT[F, Json, *], (String, Option[String], Json), (Document, Option[String], Json)] =
        Kleisli(arguments =>
          EitherT
            .fromEither(
              QueryParser.parse(arguments._1).toEither.leftMap {
                case error: SyntaxError =>
                  syntaxErrorToJson(error)
                case error =>
                  errorToJson(error)
              }
            )
            .map(document => (document, arguments._2, arguments._3))
        )

      def executeQuery: Kleisli[EitherT[F, Json, *], (Document, Option[String], Json), Json] =
        Kleisli(arguments =>
          userContext
            .flatMap(context =>
              Async[F].fromFuture(
                Async[F].delay(
                  Executor(schema)(executionContext).execute(
                    arguments._1,
                    context,
                    (),
                    arguments._2,
                    arguments._3
                  )
                )
              )
            )
            .attemptT
            .leftMap {
              case error: WithViolations =>
                executionErrorToJson(error)
              case error =>
                errorToJson(error)
            }
        )

      def query: Kleisli[EitherT[F, Json, *], Json, Json] =
        decodeQuery andThen parse andThen executeQuery

  def decodingFailureToJson(failure: DecodingFailure): Json =
    Json.obj(
      (
        "messages",
        Json.arr(
          Json.obj(
            ("message", Json.fromString(failure.message))
          )
        )
      )
    )

  def syntaxErrorToJson(error: SyntaxError): Json =
    Json.obj(
      (
        "messages",
        Json.arr(
          Json.obj(
            ("message", Json.fromString(error.getMessage)),
            (
              "locations",
              Json.arr(
                Json.obj(
                  ("line", Json.fromInt(error.originalError.position.line)),
                  ("column", Json.fromInt(error.originalError.position.column))
                )
              )
            )
          )
        )
      )
    )

  def executionErrorToJson(error: WithViolations): Json =
    Json.obj(
      (
        "messages",
        Json.fromValues(
          error
            .violations
            .map(violation =>
              Json.obj(
                ("message", Json.fromString(violation.errorMessage))
              )
            )
        )
      )
    )

  def errorToJson(error: Throwable): Json =
    Json.obj(
      (
        "messages",
        Json.arr(
          Json.obj(
            ("message", Json.fromString(Option(error.getMessage).getOrElse("")))
          )
        )
      )
    )

end GraphQL
