package training.graphql

import cats.effect._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import io.circe._
import io.circe.generic.auto._

import sangria.execution.{ Executor => Ex }
import sangria.marshalling.circe._

import org.http4s.circe._
import org.http4s.dsl.io._

import training.models.{RequestJson}
import training.graphql.Sangria.{schema}
import training.repository.{Repo}
import training.graphql.Parser

object Executor {
    implicit val decoder = jsonOf[IO, RequestJson]

    def execute(requestJson: RequestJson) = {
        val qs = requestJson.query.stripMargin
        
        Parser.parse(qs) match {
            case Right(document) => {
                val exec: Future[Json] = Ex.execute(schema, document,  new Repo)
                Ok(IO.fromFuture(IO(exec)))
            }
            case Left(err) => {
                BadRequest(s"Syntax error: ${err.getMessage}")
            }
        }
    }
}
