package models.persistient

import java.io.Serializable

import com.google.gson.annotations.Expose
import slick.jdbc.PostgresProfile.api._

import scala.annotation.meta.field

case class User(
                  @(Expose@field)
                  id: Long = 0L,
                  @(Expose@field)
                  username: String = "",
                  secret: String = "",
                  @(Expose@field)
                  name: String = "",
                  @(Expose@field)
                  surname: String = "",
                  isMale: Boolean = false,
                  isOnline: Boolean = false,
                  status: String = "",
                  latitude: Double = 0.0,
                  longitude: Double = 0.0,
                  @(Expose@field)
                  imageId: Long = 0L,
                  email:String = ""

               ) extends Serializable


class UserTable(tag: Tag) extends Table[User](tag, "user") {
   
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
   
   def username = column[String]("username", O.Unique)
   
   def secret = column[String]("secret")

   def name = column[String]("name")

   def surname = column[String]("surname")

   def isMale = column[Boolean]("is_male")

   def isOnline = column[Boolean]("is_online")

   def status = column[String]("status")

   def latitude = column[Double]("latitude")

   def longtitude = column[Double]("longtitude")

   def imageId = column[Long]("image_id",O.Unique)

   def email = column[String]("email",O.Unique)

   def * = (id, username,
      secret, name,
      surname, isMale,
      isOnline,
      status,
      latitude, longtitude,
      imageId,email) <> (User.tupled, User.unapply)
   
}

case class UserToUser(

                      userFrom: Long = 0L,
                      userTo: Long = 0L

                   )

class UserToUserTable(tag: Tag) extends Table[UserToUser](tag, "user_to_user") {

   def user_from = column[Long]("user_from")

   def user_to = column[Long]("user_to")

   def * = (user_from, user_to) <> (UserToUser.tupled, UserToUser.unapply)
   
}


case class UserToImage(
                           userId: Long,
                           imageId: Long
                        )

class UserToImageTable(tag: Tag) extends Table[UserToImage](tag, "user_image") {

   def userId = column[Long]("user_id")

   def imageId = column[Long]("image_id")

   def * = (userId, imageId) <> (UserToImage.tupled, UserToImage.unapply)
   
}