package dao.sql.tables

import models.persistient.{UserToUserRelation, UsersRelationType}
import slick.jdbc.PostgresProfile.api._
import implicits._

private[sql] class UserToUserRelationTable(tag: Tag) extends Table[UserToUserRelation](tag, "user_to_user_relation") {

   def userFrom = column[Int]("user_from")

   def userTo = column[Int]("user_to")

   def relation = column[UsersRelationType]("relation_type")

   def pk = primaryKey("user_to_user_pk",(userFrom,userTo))

   def * = (userFrom, userTo, relation) <> (UserToUserRelation.tupled, UserToUserRelation.unapply)

}
