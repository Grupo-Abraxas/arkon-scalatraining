package schemas

import sangria.schema.{Field, InterfaceType, StringType, fields}

trait Identifiable {
  def id: String
}
