package app

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import app.Config.loadConfig

object Setup {
  def transactor: Resource[IO, HikariTransactor[IO]] = {
    loadConfig match {
      case Right(config) =>
        for {
          connectEC  <- ExecutionContexts.cachedThreadPool[IO]
          transactor <- HikariTransactor.newHikariTransactor[IO](
                          config.driver,
                          config.url,
                          config.user,
                          config.password,
                          connectEC
                        )
        } yield transactor

      case Left(failures) =>
        throw new RuntimeException(
          s"Failed to load configuration: ${failures.toList.mkString(", ")}"
        )
    }
  }
}
