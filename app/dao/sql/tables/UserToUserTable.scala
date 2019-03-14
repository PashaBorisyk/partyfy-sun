package dao.sql.tables

import models.persistient.UserToUser
import slick.jdbc.PostgresProfile.api._

private[sql] class UserToUserTable(tag: Tag) extends Table[UserToUser](tag, "user_to_user") {

   def user_from = column[Long]("user_from")

   def user_to = column[Long]("user_to")

   def * = (user_from, user_to) <> (UserToUser.tupled, UserToUser.unapply)

}
