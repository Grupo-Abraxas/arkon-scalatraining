package inegi

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.circe._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax

import io.circe.generic.auto._

import database.Services
import inegi.models.InegiResponse
import training.models.{Shop, ShopInput}
object HttpClient extends IOApp {
  implicit val decoder = jsonOf[IO, List[InegiResponse]]

  def callApi(client: Client[IO], token: String): IO[List[InegiResponse]] =
    client.expect[List[InegiResponse]](uri"https://www.inegi.org.mx/app/api/denue/v1/consulta/Buscar/restaurante/21.85717833,-102.28487238/500/" / token)

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO].resource
      .use { client =>
        val r = callApi(client, "1ba9b452-4631-45f5-9452-bd31b03bdf8b").unsafeRunSync()

        for (item <- r){
          val shopInput = ShopInput(
            name = item.Nombre,
            businessName = item.Razon_social,
            activity = item.Clase_actividad,
            stratum = item.Estrato,
            address = s"${item.Tipo_vialidad} ${item.Calle} ${item.Colonia} ${item.CP}",
            phoneNumber = item.Telefono,
            email = item.Correo_e,
            website = item.Sitio_internet,
            shopType = item.Tipo,
            longitude = item.Longitud.toFloat,
            latitude = item.Latitud.toFloat
          )
          
          val shop: Shop = Services.insertShop(shopInput).unsafeRunSync()
          
          println(s"${shop.name} <- Cargado!")
        }
        
        IO.unit
      }
      .as(ExitCode.Success)
}