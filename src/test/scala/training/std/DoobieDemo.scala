package training.std

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.toFoldableOps
import doobie.Update
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import graphql.SangriaGraphExec.httpClient
import io.circe._
import model.{Alcaldia, Unidad}
import model.JsonCdmx.{AlcaldiaJson, UnidadJson}
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers


class DoobieDemo extends AnyFunSuite with  Matchers {

  implicit class Debugger[A](io: IO[A]) {
    def debug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:mtbMX",
    "userApp",
    "userAppPs"
  )


  test("consulta el estado con id 1"){
    val query = sql"select description from c_estatus where id=1".query[String].to[List].transact(xa)
    assert(query.unsafeRunSync() == List("Activo"))

  }

  test("inserta alcaldia "){

    val polygon = "{\"type\": \"Polygon\", \"coordinates\":[[[-98.99723373999994, 19.22731068999911], [-98.99744704999993, 19.227184459999123], [-98.9974635999999, 19.226488459999103], [-98.99706316999995, 19.226423519999155], [-98.99668105999993, 19.226326379999104], [-98.99642859999992, 19.22619039999913], [-98.99638268999992, 19.226128889999114]]]}"
    val query = saveAlcaldiaAutoGenerated(-1,"test",polygon,1)
    assert(query.unsafeRunSync() == 1)

  }
  def saveAlcaldiaAutoGenerated(id: Int, name: String, geopolygon: String, estado: Int): IO[Int] = {
    sql"INSERT INTO t_alcaldia (id,name, geopolygon,estado)  values ($id, $name, ST_GeomFromGeoJSON ($geopolygon),  $estado)"
      .update.run.transact(xa)
  }

  test("consulta alcaldia test"){
    val query = sql"select id, name, ST_AsGeoJSON(geopolygon) geopolygon,  estado from t_alcaldia where id=-1".query[Alcaldia].unique.transact(xa)
    assert(query.unsafeRunSync().name == "test")

  }
  test("elimina  alcaldia test"){
    val query = sql"delete from t_alcaldia where id=-1".update.run.transact(xa)
    assert(query.unsafeRunSync() == 1)

  }


  def findUnidadesByIdAlcaldia(ids: Seq[Int]): IO[List[Unidad]] = {
    val fragmentIn : Fragment = fr"IN (" ++ ids.toList.map(n =>fr"$n").intercalate(fr" , ") ++ fr")"
    val queryFr =
      fr"""select a.id, a.vehicle_id,a.point_latitude, a.point_longitude, a.geopoint,a.estado from t_mb a, t_alcaldia b
      where ST_Contains(b.geopolygon,ST_SetSRID(ST_MakePoint(a.point_longitude,a.point_latitude), 4326)  ) =true
      and b.id """ ++ fragmentIn
    println(queryFr)
    val action = queryFr.query[Unidad].stream.compile.toList
    action.transact(xa)
  }

  def findAlcaldiaByIdVehiculo(ids: Seq[Int]): IO[List[Alcaldia]] = {
    val fragmentIn : Fragment = fr"IN (" ++ ids.toList.map(n =>fr"$n").intercalate(fr" , ") ++ fr")"
    val queryFr =
      fr"""select b.id, b.name, b.geopolygon, b.estado from t_mb a, t_alcaldia b
      where ST_Contains(b.geopolygon,ST_SetSRID(ST_MakePoint(a.point_longitude,a.point_latitude), 4326)  ) =true
      and a.id """.stripMargin ++ fragmentIn
    println(queryFr)
    val action = queryFr.query[Alcaldia].stream.compile.toList
    action.transact(xa)
  }

    test("consulta  unidades by id Alcaldia"){
      val result =findUnidadesByIdAlcaldia(Seq(1,2,3)).unsafeRunSync()
      println(result)
      println(result.size)
      assert( result.size>=1)

    }

    test("consulta  Alcaldia by id unidad") {
      val result = findAlcaldiaByIdVehiculo(Seq(10, 12, 14)).unsafeRunSync()
      println(result)
      println(result.size)
      assert(result.size>=1)
    }
    test("elimina  unidades test"){
    val query = sql"delete from t_mb".update.run.transact(xa)
    assert(query.unsafeRunSync() >=0)

  }

  test("elimina  alcaldias test"){
    val query = sql"delete from t_alcaldia".update.run.transact(xa)
    assert(query.unsafeRunSync() >=0)

  }

  def unidadesDataCdmx(): IO[String] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=ad360a0e-b42f-482c-af12-1fd72140032e"
    httpClient.expect[String](target)
  }



  test("alta  unidad batch test"){

    val program: IO[String] = for {
      a <- unidadesDataCdmx
    } yield a



    val jsonStrUnidad =   parser.parse(program.unsafeRunSync()).getOrElse(Json.Null).hcursor.downField("result").downField("records").as[Json].getOrElse(Json.Null).noSpaces
    val decodeUnidad =parser.decode[List[UnidadJson]](jsonStrUnidad).getOrElse(List[UnidadJson]())
    println(decodeUnidad)


    val sql = "insert into t_mb (id, vehicle_id, point_latitude, point_longitude, geopoint,estado) values(?,?,?,?,Point(?),1)"
    val queryExec =Update[UnidadJson](sql).updateMany(decodeUnidad)
    val exec= queryExec.transact(xa).unsafeRunSync()
    println(exec)
    assert(exec >=1)

  }


  def alcaldiasDataCdmx(): IO[String] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=e4a9b05f-c480-45fb-a62c-6d4e39c5180e"
    httpClient.expect[String](target)
  }




  test("alta  alcaldia batch test"){

    val program: IO[String] = for {
      a <- alcaldiasDataCdmx
    } yield a



    val jsonStrAlcaldia =   parser.parse(program.unsafeRunSync()).getOrElse(Json.Null).hcursor.downField("result").downField("records").as[Json].getOrElse(Json.Null).noSpaces

    val decodeUnidad =parser.decode[List[AlcaldiaJson]](jsonStrAlcaldia).getOrElse(List[AlcaldiaJson]())
    println(decodeUnidad)


    val sql = "INSERT INTO t_alcaldia (id,name, geopolygon,estado)  values (?, ?, ST_GeomFromGeoJSON (?),  1)"
    val queryExec =Update[AlcaldiaJson](sql).updateMany(decodeUnidad)
    val exec= queryExec.transact(xa).unsafeRunSync()
    println(exec)
    assert(exec >=1)

  }


}