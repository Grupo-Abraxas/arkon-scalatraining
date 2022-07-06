package controller

import cats.data.Kleisli
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s._
import org.http4s.implicits._
import graphql.SangriaGraphExec
import graphql.SangriaGraphExec.httpClient
import io.circe.{Json, parser}
import model.JsonCdmx.{AlcaldiaJson, UnidadJson}
import repository.RepoMbMxImpl


object Main extends IOApp{


  val app: Kleisli[IO, Request[IO], Response[IO]] = Router(
    "/" -> SangriaGraphExec.route
  ).orNotFound

  def unidadesDataCdmx(): IO[String] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=ad360a0e-b42f-482c-af12-1fd72140032e"
    httpClient.expect[String](target)
  }
  def alcaldiasDataCdmx(): IO[String] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=e4a9b05f-c480-45fb-a62c-6d4e39c5180e"
    httpClient.expect[String](target)
  }



  override def run(args: List[String]): IO[ExitCode] = {

    val resultunidadesDataCdmx: IO[String] = for {
      a <- unidadesDataCdmx()
    } yield a

    val resultalcaldiasDataCdmx: IO[String] = for {
      a <- alcaldiasDataCdmx()
    } yield a

    val jsonStrAlcaldia =   parser.parse(resultalcaldiasDataCdmx.unsafeRunSync()).getOrElse(Json.Null).hcursor.downField("result").downField("records").as[Json].getOrElse(Json.Null).noSpaces
    val decodeAlcaldia =parser.decode[List[AlcaldiaJson]](jsonStrAlcaldia).getOrElse(List[AlcaldiaJson]())
    println(decodeAlcaldia)

    val jsonStrUnidad =   parser.parse(resultunidadesDataCdmx.unsafeRunSync()).getOrElse(Json.Null).hcursor.downField("result").downField("records").as[Json].getOrElse(Json.Null).noSpaces
    val decodeUnidad =parser.decode[List[UnidadJson]](jsonStrUnidad).getOrElse(List[UnidadJson]())
    println(decodeUnidad)

    val repo = new RepoMbMxImpl();
    repo.limpiaAlcaldias()
    repo.limpiaUnidades()
    repo.saveUnidadesBatch(decodeUnidad);
    repo.saveAlcaldiasBatch(decodeAlcaldia);

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(app)
      .resource
      .useForever
      .as(ExitCode.Success)
  }
}
