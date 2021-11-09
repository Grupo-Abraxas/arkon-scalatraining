package com.arkondata.training.repo


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import cats.effect.Sync
import cats.implicits._
import com.arkondata.training.dto.InegiResponse
import com.arkondata.training.model.{Activity, Shop}
import doobie._
import doobie.implicits._
import doobie.util.fragment.Fragment
import io.chrisdavenport.log4cats.Logger

import scala.concurrent._
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}
import spray.json.DefaultJsonProtocol._
import spray.json._
import doobie.util.ExecutionContexts
import cats._
import cats.data._
import cats.effect.Effect.ops.toAllEffectOps
import cats.effect._
import cats.implicits._


import org.postgis.Point
import doobie.postgres.pgisimplicits._

trait InegiRepo[F[_]] {

  def search(): F[List[Activity]]

  def insert(shop: Shop ): String

  def createData(): String

}

object InegiRepo {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import actorSystem.dispatcher


  def fromTransactor[F[_]: Effect] (xa: Transactor[F]): InegiRepo[F] =

    new InegiRepo[ F ] {

        val tokenInegi: String = "84482b30-34ca-4543-a30e-ed5b35afcca2"
        val select: Fragment = fr""" select id, name from comercial_activity ca """

        val request: HttpRequest = {
          val uri: String = s"""https://www.inegi.org.mx/app/api/denue/v1/consulta/BuscarEntidad/todos/20/1/100/${tokenInegi}"""
          HttpRequest( method = HttpMethods.GET, uri = uri)
        }

        def consumeApiInegi() : Future[String] = {

          val responseFuture: Future[HttpResponse] = Http().singleRequest( request )
          val entityFuture: Future[ HttpEntity.Strict ] = responseFuture.flatMap( response => response.entity.toStrict( 2.seconds ))
          entityFuture.map( entity => entity.data.utf8String )

        }

        def search(): F[List[Activity]] = select.query[ Activity ].to[List].transact( xa )



      def insert( shop: Shop ): String = {
          createData()
          "Insert"
      }


      def createData(): String = {

        consumeApiInegi().onComplete(
          v => {
            var json = ""

            v.map(  j =>  json = j )

            val response = json.parseJson.convertTo[ Seq[ InegiResponse ] ]

            response.foreach( dataInegi => {

              val nameActivity = dataInegi.claseActividad.trim
              val idActivity = createActivity( nameActivity )

              val stratum = dataInegi.estrato.trim
              val idStratum = getOrCreateStratum( stratum )

              val shopType = dataInegi.tipo.trim
              val idShopType = createShopType( shopType )

              createShop( dataInegi, idShopType, idActivity, idStratum )

            })


          }
        )

        "data created..."
      }



      def createActivity(nameActivity: String ): Int = {
        val selectActivity = sql""" select id from comercial_activity where name=$nameActivity limit 1"""
        val optionActivityId = selectActivity.query[ Int ].option.transact( xa ).toIO.unsafeRunSync()

        if ( optionActivityId.isEmpty ) {
          val insertActivitySql = sql""" insert into comercial_activity (name) values ( $nameActivity ) returning id"""
          insertActivitySql.query[ Int ].unique.transact( xa ).toIO.unsafeRunSync()
        } else {
          optionActivityId.get
        }
      }

      def getOrCreateStratum( stratum: String ): Int =  {
        val selectStratum = fr""" select id from stratum where name=$stratum limit 1 """
        val optionIdStratum = selectStratum.query[ Int ].option.transact( xa ).toIO.unsafeRunSync()

        if ( optionIdStratum.isEmpty ) {
            val insertStratum = sql"""  insert into stratum (name) values ($stratum) returning id"""
            insertStratum.query[ Int ].unique.transact( xa ).toIO.unsafeRunSync()
        } else {
          optionIdStratum.get
        }

      }

      def createShopType( shopType: String ): Int = {

        val selectShopType = fr""" select id from shop_type where name =$shopType limit 1"""
        val optionIdShopType = selectShopType.query[ Int ].option.transact( xa ).toIO.unsafeRunSync()

        if ( optionIdShopType.isEmpty ) {
          val insertShopType = sql""" insert into shop_type (name) values ($shopType) """
          insertShopType.update.run.transact(xa).toIO.unsafeRunSync()
        } else {
          optionIdShopType.get
        }

      }

      def createShop( inegiResponse: InegiResponse, idShopType: Int, idActivity: Int, idStratum: Int ) = {


        val name = inegiResponse.nombre
        val businessName = inegiResponse.razonSocial
        val address = inegiResponse.ubicacion
        val phoneNumber = inegiResponse.telefono
        val email = inegiResponse.correoE
        val webSite = inegiResponse.sitioInternet

        val long: Double = inegiResponse.longitud.toDouble
        val lat: Double = inegiResponse.latitud.toDouble
        val position: Point = new Point( long, lat )



        val insertShop =
          sql"""
            insert into shop ( name, business_name, activity_id, stratum_id, address, phone_number, email,
                              website, shop_type_id, position )
            values ( $name, $businessName, $idActivity, $idStratum, $address, $phoneNumber, $email,
                                $webSite, $idShopType, $position )
             """

        insertShop.update.run.transact( xa ).toIO.unsafeRunSync()

      }
  }


}