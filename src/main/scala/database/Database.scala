package database

import cats.effect._
import cats.implicits._

import pureconfig._
import pureconfig.generic.auto._

import doobie._
import doobie.implicits._
import doobie.hikari._

/** Configuración de datos de conexión a la Base de Datos.
 *
 *  @constructor crea la Configuración
 *  @param databaseUrl url de conexión a la base de datos
 *  @param username usuario de acceso a la base de datos
 *  @param password contraseña de acceso a la base de datos
 */
case class DatabaseConfig(databaseUrl:String, username:String, password:String)

/** Fabrica para instancias de [[database.Database]]. */
object Database {
    /** Se carga la configuración de acceso a la base de datos. */
    val config: ConfigReader.Result[DatabaseConfig] = ConfigSource.default.load[DatabaseConfig]
    val db: DatabaseConfig = config getOrElse new DatabaseConfig("jdbc:postgresql:dbname", "postgres", "")

    /** Se crea la instancia del transactor. */
    val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
        ce <- ExecutionContexts.fixedThreadPool[IO](32)
        xa <- HikariTransactor.newHikariTransactor[IO]("org.postgresql.Driver", db.databaseUrl, db.username, db.password, ce)
    } yield xa
}