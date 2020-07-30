package training.std

import cats.effect.IO
import doobie.implicits._
import shapeless.{::, HNil}

class TestQueries {
  def main(args: Array[String]): Unit = {
    val transactor = GlobalConnection.transactor
    type hListType = Int :: String :: Option[Double] :: HNil

    val query = for {
      a <- sql"select id, name, gnp from comercial_activity where id = 1".query[hListType].unique
    } yield a
    println(transactor.use(query.transact[IO]).unsafeRunSync)
  }
}
