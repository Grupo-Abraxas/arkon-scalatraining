package service

import cats.effect.IO
import domain.{Shop, ShopType}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object GraphqlService extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "data" => Ok(new ShopType(0, "retail").asJson)

//    case req @ POST -> Root / "graphql" =>
  }

}
