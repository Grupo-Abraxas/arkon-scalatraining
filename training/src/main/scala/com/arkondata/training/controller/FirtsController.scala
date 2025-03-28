package com.arkondata.training.controller

package com.arkondata.training

package com.arkondata.training

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.*
import io.circe.generic.auto.*
import org.http4s.circe.*

import org.http4s.circe.CirceEntityCodec.circeEntityDecoder


case class Persona(nombre: String, edad: Integer)


def routes =
  HttpRoutes.of[IO] {
    case request @GET -> Root / "api" / nombre=>
      Ok(s"Hola me soy $nombre")
    case request @POST -> Root / "prueba" =>
      Ok("Hola estamos en una prueba")
    case request @POST -> Root / "estatus" =>
      request.as[Persona].flatMap { persona =>
        Ok(s"Recibimos a ${persona.nombre}, tiene ${persona.edad} años.")}
  }
