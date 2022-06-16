package schemas

import models.Picture
import sangria.macros.derive.{DocumentField, ObjectTypeDescription, deriveObjectType}

object PictureType {

  implicit val PictureType =
    deriveObjectType[Unit, Picture](
      ObjectTypeDescription("The product picture"),
      DocumentField("url", "Picture CDN URL"))
}
