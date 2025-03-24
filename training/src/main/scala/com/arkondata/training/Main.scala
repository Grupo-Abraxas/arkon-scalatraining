package com.arkondata.training

import cats.effect.{IO, Resource, ResourceApp, ResourceIO}

object Main extends ResourceApp.Forever:

  def run(arguments: List[String]): ResourceIO[Unit] =
    Resource.unit[IO]

end Main
