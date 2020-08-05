package training.std

import cats.effect.{Blocker, IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

import scala.concurrent.ExecutionContext

case class GlobalConnection(
                             driver: String = "org.postgresql.Driver",
                             url: String = "jdbc:postgresql://172.17.0.2:5432/inegi",
                             user: String = "raul",
                             password: String = "raul"
                           ) {

  def connect = {
    implicit val cs = IO.contextShift(ExecutionContext.global)
    val transactor: Resource[IO, HikariTransactor[IO]] =
      for {
        ce <- ExecutionContexts.fixedThreadPool[IO](10) // our connect EC
        be <- Blocker[IO] // our blocking EC
        xa <- HikariTransactor.newHikariTransactor[IO](
          driver, url, user, password,
          ce, // await connection here
          be // execute JDBC operations here
        )
      } yield xa
    transactor
  }
}