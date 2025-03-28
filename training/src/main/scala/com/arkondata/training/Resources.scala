package com.arkondata.training

import cats.effect.{Async, Resource}
import cats.effect.std.Console
import cats.syntax.show.toShow
import fs2.io.net.Network
import natchez.Trace
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import skunk.{Session, Strategy}

object Resources:

  type SessionPool[F[_]] = Resource[F, Session[F]]

  def sessionPool[F[_]: Async: Console: Network: Trace]: Resource[F, SessionPool[F]] =
    Configuration
      .sessionPool
      .resource
      .flatMap(arguments =>
        Session.pooled(
          arguments._1.show,
          arguments._2.value,
          arguments._3,
          arguments._5,
          arguments._4,
          arguments._6,
          arguments._7,
          Strategy.BuiltinsOnly,
          arguments._8
        )
      )

  def server[F[_]: Async: Network](httpApplication: HttpApp[F]): Resource[F, Server] =
    Configuration
      .server
      .resource
      .flatMap(arguments =>
        EmberServerBuilder
          .default
          .withHost(arguments._1)
          .withPort(arguments._2)
          .withIdleTimeout(arguments._3)
          .withHttp2
          .withHttpApp(httpApplication)
          .build
      )

end Resources
