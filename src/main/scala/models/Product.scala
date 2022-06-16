package models

import schemas.Identifiable

case class Product(id: String, name: String, description: String) extends Identifiable {
  def picture(size: Int): Picture =
    Picture(width = size, height = size, url = Some(s"//cdn.com/$size/$id.jpg"))

}


