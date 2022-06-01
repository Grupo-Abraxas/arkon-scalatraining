package training.std

import training.BaseSpec
import io.circe._
import org.scalatest.EitherValues

class OptionSpec extends BaseSpec with EitherValues {

  "Json parsing" should {
    "parsing" in {
      val json = parser.parse(s""" { "a": 10 } """)

      json.value shouldBe Json.obj("a" -> Json.fromInt(10))
    }
  }

}
