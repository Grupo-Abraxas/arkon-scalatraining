package com.arkondata.training

import _root_.sangria.schema._
import cats.effect._
import cats.implicits._
import com.arkondata.training.repo.MasterRepo
import com.arkondata.training.sangria.SangriaGraphQL
import com.arkondata.training.schema.{MutationType, QueryType, WorldDeferredResolver}
import doobie._
import doobie.hikari._
import doobie.util.ExecutionContexts
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers.Location
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

object Main extends  IOApp {


  // Construct a transactor for connecting to the database.
  def transactor[F[_]: Async: ContextShift](
                                             blocker: Blocker
                                           ): Resource[F, HikariTransactor[F]] =
    ExecutionContexts.fixedThreadPool[F](10).flatMap { ce =>
      HikariTransactor.newHikariTransactor(
        "org.postgresql.Driver",
        "jdbc:postgresql:training_inegi",
        "postgres",
        "luaragl",
        ce,
        blocker
      )
    }

  // Construct a GraphQL implementation based on our Sangria definitions.
  def graphQL[F[_]: Effect: ContextShift: Logger](
                                                   transactor:      Transactor[F],
                                                   blockingContext: ExecutionContext
                                                 ): GraphQL[F] =
    SangriaGraphQL[F](
      Schema(
        query    = QueryType[F],
        mutation = Some(MutationType[F])
      ),
      WorldDeferredResolver[F],
      MasterRepo.fromTransactor(transactor).pure[F],
      blockingContext
    )

  // Playground or else redirect to playground
  def playgroundOrElse[F[_]: Sync: ContextShift](
                                                  blocker: Blocker
                                                ): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._
    HttpRoutes.of[F] {

      case GET -> Root / "playground.html" =>
        StaticFile
          .fromResource[F]("/assets/playground.html", blocker)
          .getOrElseF(NotFound())

      case _ =>
        PermanentRedirect(Location(Uri.uri("/playground.html")))

    }
  }

  // Resource that mounts the given `routes` and starts a server.
  def server[F[_]: ConcurrentEffect: ContextShift: Timer](
                                                           routes: HttpRoutes[F]
                                                         ): Resource[F, Server[F]] =
    BlazeServerBuilder[F]( ExecutionContext.global )
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .resource

  // Resource that constructs our final server.
  def resource[F[_]: ConcurrentEffect: ContextShift: Timer](
                                                             implicit L: Logger[F]
                                                           ): Resource[F, Server[F]] =
    for {
      b   <- Blocker[F]
      xa  <- transactor[F](b)
      gql  = graphQL[F](xa, b.blockingContext)
      rts  = GraphQLRoutes[F](gql) <+> playgroundOrElse(b)
      svr <- server[F](rts)
    } yield svr

  // Our entry point starts the server and blocks forever.
  def run(args: List[String]): IO[ExitCode] = {
    implicit val log = Slf4jLogger.getLogger[IO]
    resource[IO].use(_ => IO.never.as(ExitCode.Success))
  }
}
