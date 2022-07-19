package training.database

import cats.effect.Async
import doobie.Transactor

case class Manager[F[_]](
  activities: ActivityQuery[F],
  stratums: StratumQuery[F],
  shopCategories: ShopCategoryQuery[F],
  shops: ShopQuery[F]
)

object Manager {

  def fromTransactor[F[_]: Async](transactor: Transactor[F]): Manager[F] =
    Manager[F](
      ActivityQuery.fromTransactor(transactor),
      StratumQuery.fromTransactor(transactor),
      ShopCategoryQuery.fromTransactor(transactor),
      ShopQuery.fromTransactor(transactor)
    )
}
