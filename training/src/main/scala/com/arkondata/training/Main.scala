package com.arkondata.training

import cats.effect.{IO, ResourceApp, ResourceIO, Resource}

object Main extends ResourceApp.Forever:

  def run(arguments: List[String]): ResourceIO[Unit] =
    Resource.unit[IO]

end Main
