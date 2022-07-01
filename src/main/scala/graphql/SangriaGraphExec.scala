package graphql

import cats.effect.IO
import graphql.SangriaGraphql.SchemaEstado
import io.circe.{Json, JsonObject}
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.impl.IntVar
import org.http4s.dsl.io.{->, /, BadRequest, GET, Ok, POST, Root}
import org.http4s.dsl.io._
import org.http4s.circe._
import repository.EstadoRepo
import sangria.ast.Document
import sangria.execution._
import sangria.marshalling._
import sangria.parser.{QueryParser, SyntaxError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import sangria.marshalling.circe._


object SangriaGraphExec {

  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name => Ok(s"Hello, $name.")
    //case GET -> Root / "estados" => Ok(new EstadoRepo().findAllEstado().asJson)
    //case GET -> Root / "estado" / IntVar(id) => Ok(new EstadoRepo().findEstadoById(id).asJson)
    case request@POST -> Root / "graphql" =>
      request.as[Json].flatMap { jsonBody =>
        val jsonBodyhcursor = jsonBody.hcursor
        val query = jsonBodyhcursor.downField("query").as[String] match {
          case Right(q) => q.toString
          case Left(q) => ""
        }
        val operationName = jsonBodyhcursor.downField("operationName").as[String] match {
          case Right(q) => q.toString
          case Left(q) => ""
        }
        val variables = jsonBodyhcursor.downField("variables").as[String] match {
          case Right(q) => q.toString
          case Left(q) => ""
        }
        println("query : ".concat(query))
        println("operationName : ".concat(operationName))
        println("variables : ".concat(variables))

        //val resultado  =Executor.execute( schema,query)
        /*QueryParser.parse(query) match {
          case Success(query) =>
            Ok("saludos!".concat(query.toString) )
          case Failure(error: SyntaxError) =>
            BadRequest("erorr!".concat(error.getMessage()) )
        }*/
        QueryParser.parse(query) match {

          case Success(query) =>
            val result :Future[Json] = Executor.execute(SchemaEstado, query, new EstadoRepo)
            Try(Await.result(result, 10 seconds)) match {
              case Success(result) => {
                println(result)
                Ok(result)
              }
              case Failure(_) => BadRequest("Failure Happened")
            }

          case Failure(error: SyntaxError) => {
            println("falla")
            BadRequest("erorr!".concat(error.getMessage()))
          }
          case _ => BadRequest("Nada")
        }
      }

  }
}
