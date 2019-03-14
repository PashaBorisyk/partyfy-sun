package dao.sql.tables

import slick.jdbc.PostgresProfile.api._
import models.persistient.UserToImage

private[sql] class UserToImageTable(tag: Tag) extends Table[UserToImage](tag, "user_to_image") {

   def userId = column[Long]("user_id")

   def imageId = column[Long]("image_id")

   def * = (userId, imageId) <> (UserToImage.tupled, UserToImage.unapply)

}
