package training

import cats.effect._
import cats.effect.std.Dispatcher
import cats.implicits._
import com.comcast.ip4s._
import doobie._
import doobie.hikari._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.ember.server._
import org.http4s.server._
import sangria.schema.Schema

import training.database.Manager
import training.graph.{ Graph, MutationType, QueryType }

object Main extends IOApp {
  implicit val executionContext = unsafe.IORuntime.global.compute

  def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool[F](32)
      ht <- HikariTransactor.newHikariTransactor[F](
              "org.postgresql.Driver",
              "jdbc:postgresql:training",
              "admin",
              "password",
              ec
            )
    } yield ht

  def graph[F[_]: Async](dispatcher: Dispatcher[F], transactor: Transactor[F]): Graph[F] =
    Graph[F](
      Schema(query = QueryType[F](dispatcher), mutation = Some(MutationType[F](dispatcher))),
      Manager.fromTransactor(transactor).pure[F],
      executionContext
    )

  def graphRoutes[F[_]: Async](graph: Graph[F]): HttpRoutes[F] = {
    object Dsl extends Http4sDsl[F]
    import Dsl._

    HttpRoutes.of[F] {
      case request @ GET -> Root / "api" / "graph" =>
        StaticFile fromResource ("/assets/playground.html", Some(request)) getOrElseF (NotFound())
      case request @ POST -> Root / "api" / "graph" =>
        request.as[Json].flatMap(graph.parse).flatMap {
          case Right(json) => Ok(json)
          case Left(json)  => BadRequest(json)
        }
    }
  }

  def server[F[_]: Async](routes: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .build

  def builder[F[_]: Async]: Resource[F, Server] =
    for {
      d <- Dispatcher[F]
      t <- transactor[F]
      g  = graph[F](d, t)
      gr = graphRoutes[F](g)
      s <- server[F](gr)
    } yield s

  def run(args: List[String]): IO[ExitCode] =
    builder[IO].use(_ => IO.never).as(ExitCode.Success)
}
