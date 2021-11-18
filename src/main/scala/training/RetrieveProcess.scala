package training

import cats.effect._
import io.circe.generic.auto._
import io.circe.literal._
import org.http4s.Method.POST
import org.http4s.Status.Successful
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.{Request, Uri}
import training.model.inegi.Shop

import scala.concurrent.ExecutionContext.global

object RetrieveProcess extends IOApp with Http4sClientDsl[IO] {
  def postToServer(client: Client[IO], shop: Shop): IO[String] = {
    val request: IO[Request[IO]] = POST(
      json"""
        {
          "query": "mutation CreateShop($$input, CreateShopInput) {createShop(input: $$input) {id}}",
          "variables": {
            "input": {
              "id": ${shop.Id},
              "name": ${shop.Nombre},
              "businessName": ${shop.Razon_social},
              "activity": ${shop.Clase_actividad},
              "stratum": ${shop.Estrato},
              "address": ${shop.Calle + shop.Num_Exterior + shop.Colonia + shop.Ubicacion},
              "phoneNumber": ${shop.Telefono},
              "email": ${shop.Correo_e},
              "website": ${shop.Sitio_internet},
              "shopType": ${shop.Tipo},
              "lat": ${shop.Latitud},
              "long": ${shop.Longitud}
            }
          }
        }
      """,
      Uri.uri("http://127.0.0.1:8080/graphql")
    )
    client.expect[String](request)
  }

  def getDataINEGI(
      client: Client[IO],
      lat: Float,
      lng: Float
  ): IO[Unit] = IO {
    val url =
      Uri.uri("https://www.inegi.org.mx/app/api/denue/v1/consulta/Buscar")
    val shops = client
      .get(
        url.withPath(s"/todos/$lat,$lng/5000/${System.getenv("INEGI_TOKEN")}")
      ) { case Successful(resp) => resp.decodeJson[List[Shop]] }
      .unsafeRunSync()

    for (shop <- shops) {
      postToServer(client, shop).unsafeRunSync()
    }
  }

  def run(args: List[String]): IO[ExitCode] = {
    val List(lat, lng) = args
    BlazeClientBuilder[IO](global).resource
      .use(client => getDataINEGI(client, lat.toFloat, lng.toFloat))
      .as(ExitCode.Success)
  }
}
