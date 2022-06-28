import cats.effect.{IO, IOApp}
object HellowWorld  extends IOApp .Simple {
  override def run: IO[Unit] =
    {

      for {
        _ <- IO.println("Hello")
        _ <- IO.println("World")
      } yield ()
      IO.println("Hello") >> IO.println("World")

    }
}
