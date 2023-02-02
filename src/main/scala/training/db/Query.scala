package training.db

import doobie._
import doobie.implicits._

import training.models.{Product}

object Query {
    def find(id: Int): ConnectionIO[Option[Product]] =
        sql"select * from products where id=$id".query[Product].option

    def findAll(): ConnectionIO[List[Product]] =
        sql"select * from products".query[Product].to[List]

    def addProduct(name: String, description: String): ConnectionIO[Product] =
        for {
            _  <- sql"insert into products (name, description) values ($name, $description)".update.run
            id <- sql"select lastval()".query[Long].unique
            p  <- sql"select id, name, description from products where id = $id".query[Product].unique
        } yield p
}
