package training

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object Routes {

  def apiRoutes[F[_]: Sync](H: ApiRes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "api" / "status" =>
        for {
          message <- H.apiMessage()
          resp <- Ok(message)
        } yield resp
    }
  }

}
