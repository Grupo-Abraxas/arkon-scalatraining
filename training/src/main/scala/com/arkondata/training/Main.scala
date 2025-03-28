package com.arkondata.training

import cats.effect.{IO, ResourceApp, ResourceIO}

import scala.language.postfixOps
import cats.syntax.functor.toFunctorOps
import com.arkondata.training.controller.com.arkondata.training.com.arkondata.training.routes

object Main extends ResourceApp.Forever:

  def run(arguments: List[String]): ResourceIO[Unit] =
    Resources.server[IO](routes.orNotFound).void
end Main

