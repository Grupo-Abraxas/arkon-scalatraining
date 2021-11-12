package service

import cats.effect.IO
import domain.{Shop, ShopType}
import fs2.Stream
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.headers.`Content-Type`

object GraphqlService extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "data" => Ok(repository.Postgres.getShopById(1).asJson)

    case GET -> Root / "graphql" => Ok(
      repository.Postgres.getShops()
    )

//    case req @ POST -> Root / "graphql" =>
  }

}
