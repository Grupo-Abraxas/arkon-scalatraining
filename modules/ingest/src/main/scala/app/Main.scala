package app

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.unsafe.implicits.global
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.implicits._
import org.http4s.blaze.client.BlazeClientBuilder
import io.circe.Json
import io.circe.parser._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val apiUrl = "https://datos.cdmx.gob.mx/api/3/action/datastore_search?limit=11750&resource_id=98f51fe2-18cb-4f50-a989-b9f81a2b5a76"

    val transactorResource = HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://postgres:5432/wifi_db",
      "postgres",
      "password",
      ExecutionContexts.synchronous
    )

    BlazeClientBuilder[IO].resource.use { client =>
      transactorResource.use { transactor =>
        for {
          _ <- IO.println("Fetching data from API...")
          response <- client.expect[String](apiUrl)
          json <- IO.fromEither(parse(response))
          records <- IO.fromEither(json.hcursor
            .downField("result")
            .downField("records")
            .as[List[Json]])
          _ <- records.traverse { record =>
            val id = record.hcursor.get[Int]("_id").getOrElse(0)
            val program = record.hcursor.get[String]("programa").getOrElse("unknown")
            val date = record.hcursor.get[String]("fecha_instalacion").getOrElse("NA")
            val latitude = record.hcursor.get[Double]("latitud").getOrElse(0.0)
            val longitude = record.hcursor.get[Double]("longitud").getOrElse(0.0)
            val neighborhood = record.hcursor.get[String]("colonia").getOrElse("unknown")
            val municipality = record.hcursor.get[String]("alcaldia").getOrElse("unknown")

            sql"""
                 |INSERT INTO wifi_points (id, program, installation_date, latitude, longitude, neighborhood, municipality)
                 |VALUES ($id, $program, $date, $latitude, $longitude, $neighborhood, $municipality)
            """.stripMargin.update.run.transact(transactor)

          }.void
          _ <- IO.println("Ingest process completed successfully!")
        } yield ()
      }
    }.as(ExitCode.Success)
  }
}
