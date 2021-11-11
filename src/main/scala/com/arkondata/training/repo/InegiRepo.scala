package com.arkondata.training.repo



import cats.effect.Effect.ops.toAllEffectOps
import cats.effect.{Blocker, _}
import com.arkondata.training.dto.InegiResponse
import doobie._


import org.http4s.circe.jsonOf
import org.http4s.client._
import org.http4s.{EntityDecoder, Method, Request, Uri}

import java.util.concurrent._
import scala.concurrent.ExecutionContext.global

trait InegiRepo[F[_]] {

  def createData(id: Int): F[ String ]

}

object InegiRepo {



  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(5)
  val blocker: Blocker = Blocker.liftExecutorService(blockingPool)
  val httpClient: Client[IO] = JavaNetClientBuilder[IO](blocker).create

  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): InegiRepo[F] =

    new InegiRepo[ F ] {

      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
      val tokenInegi: String = "84482b30-34ca-4543-a30e-ed5b35afcca2"

      def consume(): IO[List[InegiResponse]] = {
        val uri = Uri( path = "https://www.inegi.org.mx/app/api/denue/v1/consulta/BuscarEntidad/todos/20/1/10/" ++ tokenInegi )
        val request = Request[IO] ( method = Method.GET, uri = uri )

        implicit val inegiDecoder: EntityDecoder[ IO, List[InegiResponse] ] = jsonOf[ IO, List[InegiResponse] ]

        httpClient.expect[ List[ InegiResponse] ]( request )
      }

      def createData(id: Int): F[ String ] = {

        consume().toIO.unsafeToFuture().onComplete( v => {
          v.get.foreach( println )
        })

        Effect.apply[ F ].pure( " data created..." )
      }

  }


}