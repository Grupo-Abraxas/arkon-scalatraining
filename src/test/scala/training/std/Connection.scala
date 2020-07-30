package training.std

import cats.effect.{Blocker, IO, Resource}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object GlobalConnection {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](10) // our connect EC
      be <- Blocker[IO] // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver", // driver classname
        "jdbc:postgresql://172.17.0.2:5432/inegi", // connect URL
        "raul", // username
        "raul", // password
        ce, // await connection here
        be // execute JDBC operations here
      )
    } yield xa
}
