// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package training.schema

import cats.effect.Effect
import cats.implicits.toFunctorOps
import sangria.execution.deferred.{Deferred, DeferredResolver}
import training.repo.MasterRepo

import scala.concurrent._
import scala.reflect.ClassTag
import scala.util.Success

object WorldDeferredResolver {

  def apply[F[_]: Effect]: DeferredResolver[MasterRepo[F]] =
    new DeferredResolver[MasterRepo[F]] {

      def resolve(
          deferred: Vector[Deferred[Any]],
          ctx: MasterRepo[F],
          queryState: Any
      )(implicit
          ec: ExecutionContext
      ): Vector[Future[Any]] = {

        // So what we're going to do is create a map of Deferred to Promise and the complete them
        // asynchronously through various batch queries. We need to figure out how to fail them when
        // something bad happens but we'll be optimistic for now.

        // Deduplicate our Deferreds and associate each with an unfulfilled Promise
        // Note that computing this is unsafe because reference equality matters for Promises.
        val promises: Map[Deferred[Any], Promise[Any]] =
          deferred.map(d => d -> Promise[Any]()).toMap

        // Select the distinct Deferreds of the given class. This is feckin desperate but we're
        // given Any so not a whole lot of choices.
        def select[A <: Deferred[Any]: ClassTag]: List[A] =
          promises.keys.collect { case a: A => a }.toList

        // Complete the promise associated with a Deferred
        def complete[A](d: Deferred[A], a: A): F[Unit] =
          Effect[F].delay(promises(d).complete(Success(a))).void

        // Anyway we're done here.
        deferred.map(promises(_).future)
      }
    }
}
