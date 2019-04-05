package dao.sql.tables

import models.persistient.UserToImage
import slick.jdbc.PostgresProfile.api._

private[sql] class UserToImageTable(tag: Tag)
   extends Table[UserToImage](tag, "user_to_image") {

   def userID = column[Int]("user_id")

   def imageID = column[Long]("image_id")

   def is_marked = column[Boolean]("is_marked")

   def marker_id = column[Int]("marker_id")

   def x = column[Float]("x")

   def y = column[Float]("y")

   def pk = primaryKey("user_to_image_pk", (userID, imageID))

   def * =
      (userID, imageID, is_marked, marker_id, x, y) <> (UserToImage.tupled, UserToImage.unapply)

}
