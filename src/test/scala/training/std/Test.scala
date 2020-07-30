package training.std

import scala.util.Random

class Test {}

object Test {
  def main(args: Array[String]): Unit = {
    println {
      "Insert All: %s".format(
        Shop.createAll(
          ComercialActivity(1, "Tienda"),
          ShopType(1, "Type"),
          Stratum(1, "Stratum"),
          Shop(
            Random.nextInt(),
            "Loncheria",
            "business_name",
            1,
            1,
            "address",
            "phone_number",
            "email",
            "website",
            1,
            "POINT(19.3917104 -80.8880504)"
          )))
    }

/*    println {
      "Shop Insert: %s".format(Shop.createShop(Shop(
        Random.nextInt(),
        "Loncheria",
        "business_name",
        1,
        1,
        "address",
        "phone_number",
        "email",
        "website",
        1,
        "POINT(19.3917104 -80.8880504)"
      )))
    }

    println {
      "Shop All: " +
        Shop.findAll()
    }

    println {
      "Shop find by id: " +
        Shop.findById(1)
    }*/

    /* println{
       "ComercialActivity ById: " +
        ComercialActivity.findById(1).name
     }
     println {
       "ComercialActivity Insert: " +
        ComercialActivity.createComercialActivity(ComercialActivity(Random.nextInt(), Random.nextString(5)))
     }*/
  }
}