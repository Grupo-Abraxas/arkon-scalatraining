package training.std

import training.BaseSpec

class OptionSpec extends BaseSpec {
  "Option" should "behave" in {
    val someValue: Option[String] = Some("I am wrapped in something")
    someValue should be(Some("I am wrapped in something"))

    val emptyValue: Option[String] = None
    emptyValue should be(None)
  }

}
