package com.arkondata.training.repo

import cats.effect.implicits.toEffectOps
import cats.effect.{Blocker, _}
import com.arkondata.training.dto.InegiResponse
import doobie.util.transactor.Transactor
import org.http4s.circe.jsonOf
import org.http4s.client._
import org.http4s.{EntityDecoder, Method, Request, Uri}

import java.util.concurrent._


trait InegiRepo[F[_]] {

  def consumer( xa: Transactor[ F ] ): F[ Unit ]

}

object InegiRepo {


  def fromTransactor[F[_]: Effect] (implicit  cs: ContextShift[F], t: Timer[ F ]): InegiRepo[F] =

    new InegiRepo[ F ] {

      val blockingPool: ExecutorService = Executors.newFixedThreadPool(5)
      val blocker: Blocker = Blocker.liftExecutorService(blockingPool)
      val httpClient: Client[F] = JavaNetClientBuilder[F](blocker).create
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
      val tokenInegi: String = "84482b30-34ca-4543-a30e-ed5b35afcca2"

      def consumer( xa : Transactor[F] ): F[ Unit ] = {
        val uri = Uri( path = "https://www.inegi.org.mx/app/api/denue/v1/consulta/BuscarEntidad/todos/20/1/100/" ++ tokenInegi )
        val request = Request[F] ( method = Method.GET, uri = uri )

        implicit val inegiDecoder: EntityDecoder[ F, List[InegiResponse] ] = jsonOf[ F , List[InegiResponse] ]
        val shopRepository = ShopRepository.fromTransactor( xa )

        httpClient.expect[ List[ InegiResponse] ]( request )
            .toIO
            .unsafeToFuture
            .onComplete( v => v.get.foreach( shopRepository.createShopFromInegi ) )

        Effect.apply.pure()
      }

  }


}