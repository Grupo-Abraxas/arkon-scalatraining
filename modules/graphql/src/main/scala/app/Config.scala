package app

import pureconfig._
import pureconfig.generic.auto._
import pureconfig.error.ConfigReaderFailures

case class DatabaseConfig(driver: String, url: String, user: String, password: String)

object Config {
  def loadConfig: Either[ConfigReaderFailures, DatabaseConfig] = {
    ConfigSource.resources("application.conf").at("database").load[DatabaseConfig]
  }
}
