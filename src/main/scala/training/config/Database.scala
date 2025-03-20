package config

import cats.effect.{IO, Resource}
import doobie.util.transactor.Transactor
import com.typesafe.config.{Config, ConfigFactory}

object DatabaseConfig {

  private val config: Config = ConfigFactory.load()
  private val dbConfig: Config = config.getConfig("db")

  def createTransactor(): Resource[IO, Transactor[IO]] = {
    val transactor: Transactor[IO] = Transactor.fromDriverManager[IO](
      dbConfig.getString("driver"),
      dbConfig.getString("url"),
      dbConfig.getString("user"),
      dbConfig.getString("password")
    )

    Resource.pure(transactor)
  }
}
