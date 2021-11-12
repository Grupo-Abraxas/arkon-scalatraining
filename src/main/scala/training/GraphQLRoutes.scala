// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package training

import cats.effect.{ContextShift, Sync}
import cats.implicits.toFlatMapOps
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe.{jsonDecoder, jsonEncoder}
import org.http4s.dsl.Http4sDsl

object GraphQLRoutes {

  /** An `HttpRoutes` that maps the standard `/graphql` path to a `GraphQL` instance */
  def apply[F[_]: Sync: ContextShift](
      graphQL: GraphQL[F]
  ): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._
    HttpRoutes.of[F] { case req @ POST -> Root / "graphql" ⇒
      req.as[Json].flatMap(graphQL.query).flatMap {
        case Right(json) => Ok(json)
        case Left(json)  => BadRequest(json)
      }
    }
  }

}
