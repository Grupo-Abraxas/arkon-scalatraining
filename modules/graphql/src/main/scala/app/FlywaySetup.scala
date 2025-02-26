package app

import cats.effect.IO
import org.flywaydb.core.Flyway

object FlywaySetup {
  def migrate(databaseUrl: String, user: String, password: String): IO[Unit] = IO {
    val flyway = Flyway.configure()
      .dataSource(databaseUrl, user, password)
      .locations("classpath:db/migration")
      .load()

    val migrations = flyway.migrate()
    println(s"Successfully applied $migrations migrations")
  }
}
