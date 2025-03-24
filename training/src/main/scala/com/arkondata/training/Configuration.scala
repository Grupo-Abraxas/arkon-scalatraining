package com.arkondata.training

import scala.concurrent.duration.FiniteDuration

import cats.arrow.FunctionK
import cats.effect.{Async, Resource}
import cats.syntax.parallel.{catsSyntaxTuple3Parallel,catsSyntaxTuple8Parallel}
import ciris.{ConfigDecoder, ConfigValue, Effect, env}
import ciris.http4s.{hostConfigDecoder, portConfigDecoder}
import com.comcast.ip4s.{Host, Port}
import skunk.SSL



type Configuration[A] = ConfigValue[Effect, A]

object Configuration:



  val sessionPool =
    (
      env("DATABASE_HOST").as[Host],
      env("DATABASE_PORT").as[Port],
      env("DATABASE_USER"),
      env("DATABASE_PASSWORD").option,
      env("DATABASE_NAME"),
      env("DATABASE_MAX_CONNECTIONS").as[Int],
      env("DATABASE_DEBUG").as[Boolean],
      ConfigValue.default(SSL.None)
    )
    .parTupled



  val server =
    (
      env("HTTP_SERVER_HOST").as[Host],
      env("HTTP_SERVER_PORT").as[Port],
      env("HTTP_SERVER_IDLE_TIME_OUT").as[FiniteDuration]
    )
    .parTupled

  def liftResource[F[_]: Async]: FunctionK[Configuration, Resource[F, *]] =
    new FunctionK[Configuration, Resource[F, *]]:

      def apply[A](value: Configuration[A]): Resource[F, A] =
        value.resource

end Configuration
