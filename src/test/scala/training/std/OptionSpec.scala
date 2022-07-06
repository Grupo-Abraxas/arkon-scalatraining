package training.std

import training.BaseSpec
import io.circe._
import org.scalatest.EitherValues
import cats._
import cats.implicits._
import io.circe.syntax._
import cats.Semigroup
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import graphql.SangriaGraphExec.httpClient
import model.Estado
import org.http4s.circe.jsonDecoder
import org.http4s.implicits.http4sLiteralsSyntax
import sangria.marshalling.circe._

import scala.collection.immutable.ListMap


class OptionSpec extends BaseSpec with EitherValues {


  "Json parsing" should {
    "parsing" in {
      val json = parser.parse(s""" { "a": 10 } """)

      json.value shouldBe Json.obj("a" -> Json.fromInt(10))
    }
  }


  "Prueba" should {
    "evaluacion" in {
      assert(List(1,2,3)
        .map(x => x + 1)  == List(2,3,4))
    }
  }

  "Prueba2" should {
    "evaluacion  option" in {
      assert(List(1,2,3)
        .map(x => x + 1)  == List(2,3,4))
    }
  }

  "Prueba cats" should {
    "evaluacion  " in {
      assert( Semigroup[Int].combine(1, 2) == 3)
    }
  }
  "Prueba cats2" should {
    "evaluacion  " in {
      assert(Semigroup[Option[Int]].combine(Option(1), None) == Some(1))
    }
  }


  "Prueba cats3" should {
    "evaluacion  " in {
      val aMap = Map("foo" -> Map("bar" -> 5))
      val anotherMap = Map("foo" -> Map("bar" -> 6))
      val combinedMap = Semigroup[Map[String, Map[String, Int]]].combine(aMap, anotherMap)
      assert(combinedMap.get("foo")  == Some(Map("bar" -> 11)))
    }
  }

  "Prueba cats4" should {
    "evaluacion  " in {
      val one: Option[Int] = Option(1)
      val two: Option[Int] = Option(2)
      val n: Option[Int] = None
      assert((one |+| two )  == Some(3))
    }
  }
  "Prueba cats5" should {
    "evaluacion  " in {

      assert(Monoid[String].combineAll(List("a", "b", "c")) == "abc")
    }
  }

  "Prueba 6" should {
    "evaluacion  " in {

      assert(Monoid[Map[String, Int]].combineAll(List()) == Map())
    }
  }
  "Prueba cat7" should {
    "evaluacion  " in {
      val l = List(1, 2, 3, 4, 5)

      assert(l.foldMap(i => i.toString)  == "12345")
    }
  }
  "Prueba cat8" should {
    "evaluacion  " in {
      val l = List(1, 2, 3, 4, 5)

      assert(l.foldMap(i => (i, i.toString)) == (15,"12345"))
    }
  }




  def unidadesDataCdmx(): IO[Json] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=ad360a0e-b42f-482c-af12-1fd72140032e"
    httpClient.expect[Json](target)
  }
  def alcaldiasDataCdmx(): IO[Json] = {
    val target = uri"https://datos.cdmx.gob.mx/api/3/action/datastore_search?resource_id=e4a9b05f-c480-45fb-a62c-6d4e39c5180e"
    httpClient.expect[Json](target)
  }

  "consumir endpoint Data" should {
    "  consume unidades " in {

      val program: IO[Json] = for {
        a <- unidadesDataCdmx
      } yield a
      val data = program.unsafeRunSync();
      val result = data.findAllByKey("result")
      val records = result.asJson.findAllByKey("records")

      println(records)
      assert("1" == "")
    }
  }

}
