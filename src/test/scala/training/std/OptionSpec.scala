package training.std

import training.{BaseSpec, DoobieDemo}
import io.circe._
import org.scalatest.EitherValues
import cats._
import cats.implicits._
import io.circe.syntax._
import cats.Semigroup
import model.Estado

import scala.collection.immutable.ListMap


class OptionSpec extends BaseSpec with EitherValues {


  "Json parsing" should {
    "parsing" in {
      val json = parser.parse(s""" { "a": 10 } """)

      json.value shouldBe Json.obj("a" -> Json.fromInt(10))
    }
  }
/*
  "Consulta BD" should {
    "Lista  estatus vacio" in {
      assert(DoobieDemo.findAllEstatus.unsafeRunSync().nonEmpty)
    }
  }*/

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


  "Prueba cat9" should {
    "evaluacion  " in {

      val l = ListMap("data" -> ListMap("estados" -> Vector(ListMap("id" -> 1, "description" -> "Activo"), ListMap("id" -> 2, "description" -> "Inactivo"))))
      val jsonStr = Json.fromString(l.toString())
      println(jsonStr)
      assert(l == (15,12345))
    }
  }

}
