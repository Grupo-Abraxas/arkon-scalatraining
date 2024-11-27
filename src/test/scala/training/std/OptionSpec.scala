package training.std

import io.circe._
import org.scalatest.EitherValues
import training.BaseSpec

class OptionSpec extends BaseSpec with EitherValues {

  "Json parsing" should {
    "parsing" in {
      val json = parser.parse(s""" { "a": 10 } """)

      json.value shouldBe Json.obj("a" -> Json.fromInt(10))
    }
  }

}
