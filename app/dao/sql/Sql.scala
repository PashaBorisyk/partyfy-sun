package dao.sql

import slick.jdbc.PostgresProfile.api._

class Sql

object Sql {
   def apply[T](t: T) = SimpleDBIO(_ => t)
}
