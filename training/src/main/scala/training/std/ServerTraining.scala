package training.std

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.duration._
import Console._
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object ServerTraining extends App {

  val PORT = 8080

  implicit val actorSystem = ActorSystem("graphql-server")
  implicit val materializer = ActorMaterializer()

  logger("Starting GRAPHQL Server Training...")

  //shutdown Hook
  scala.sys.addShutdownHook(() -> shutdown())

  val route: Route =
    (post & path("graphql")){
      entity(as[JsValue]){ requestJson =>
        GraphQLServer.endpoint(requestJson)
      }
    } ~ {
      getFromResource("../../../../common/src/main/resources/graphiql.html")
    }

  Http().bindAndHandle(route, "0.0.0.0", PORT)

  logger(s"open a browser with URL: http://localhost:$PORT")
  logger(s"or POST queries to http://localhost:$PORT/graphql")

  def shutdown(): Unit = {
    logger("Terminating...", YELLOW)
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
    logger("Terminated... Bye", YELLOW)
  }

  private def logger(message: String, color: String = GREEN): Unit = {
    println(color + message)
  }
}