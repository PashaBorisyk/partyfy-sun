package dao.sql.tables

import slick.jdbc.PostgresProfile.api._
import models.persistient.UserToImage

private[sql] class UserToImageTable(tag: Tag) extends Table[UserToImage](tag, "user_to_image") {

   def userId = column[Int]("user_id")

   def imageId = column[Long]("image_id")

   def is_marked = column[Boolean]("is_marked")

   def x = column[Float]("x")

   def y = column[Float]("y")

   def * = (userId, imageId,is_marked,x,y) <> (UserToImage.tupled, UserToImage.unapply)

}
