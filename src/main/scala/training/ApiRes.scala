package training

import cats.Applicative
import cats.implicits._
import io.circe.generic.auto.exportEncoder
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait ApiRes[F[_]]{
  def apiMessage(): F[ApiRes.Message]
}

object ApiRes {
  final case class Message(message: String) extends AnyVal

  object Message {
    implicit val messageEncoder: Encoder[Message] = new Encoder[Message] {
      final def apply(a: Message): Json = Json.obj(
        ("message", Json.fromString(a.message)),
      )
    }
    implicit def messageEntityEncoder[F[_]]: EntityEncoder[F, Message] =
      jsonEncoderOf[F, Message]
  }

  def impl[F[_]: Applicative]: ApiRes[F] = new ApiRes[F]{
    def apiMessage(): F[ApiRes.Message] =
      Message("Bienvenido").pure[F]
  }
}
