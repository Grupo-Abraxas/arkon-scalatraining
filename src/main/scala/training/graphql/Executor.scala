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

import training.models.{RequestJson}
import training.graphql.Sangria.{schema}
import training.repository.{Repo}
import training.graphql.Parser

object Executor {
    def execute(requestJson: RequestJson) = {
        val qs = requestJson.query.stripMargin
        
        Parser.parse(qs) match {
            case Right(document) => {
                val result: Future[Json] = Ex.execute(schema, document,  new Repo)
                // Ok(IO.fromFuture(IO(exec)))
                Try(Await.result(result, 10 seconds)) match {
                    case Success(result) => {
                        Ok(result)
                    }
                    case Failure(error) => {
                        BadRequest(s"Error: ${error.getMessage}")
                    }
                }
            }
            case Left(err) => {
                BadRequest(s"Syntax error: ${err.getMessage}")
            }
        }
    }
}
