package training.config

import cats.effect.{Async, Blocker, ContextShift, Resource}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor


object DatabaseConfig {
  def transactor[F[_] : Async : ContextShift](
    blocker: Blocker,
  ): Resource[F, HikariTransactor[F]] =
    ExecutionContexts.fixedThreadPool[F](10).flatMap { ce =>
      HikariTransactor
        .newHikariTransactor[F](
          "org.postgresql.Driver",
          "jdbc:postgresql:training",
          "user",
          "password",
          ce,
          blocker
        )
    }
}
