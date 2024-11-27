package graphql.app

import pureconfig._
import pureconfig.generic.auto._

case class DatabaseConfig(driver: String, url: String, user: String, password: String)

object Config {
  def loadConfig: Either[pureconfig.error.ConfigReaderFailures, DatabaseConfig] =
    ConfigSource.default.at("database").load[DatabaseConfig]
}