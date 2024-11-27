package graphql.app

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import pureconfig.error.ConfigReaderException

object Setup {
  def transactor: Resource[IO, HikariTransactor[IO]] = {
    Config.loadConfig match {
      case Right(config) =>
        for {
          connectEC <- ExecutionContexts.cachedThreadPool[IO]
          xa <- HikariTransactor.newHikariTransactor[IO](
            config.driver,
            config.url,
            config.user,
            config.password,
            connectEC
          )
        } yield xa

      case Left(error) =>
        throw new ConfigReaderException[DatabaseConfig](error)
    }
  }
}
