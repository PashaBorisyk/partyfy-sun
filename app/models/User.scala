package models

import java.io.Serializable

import com.google.gson.annotations.Expose
import slick.jdbc.PostgresProfile.api._

case class User(
   @Expose
   id:Long = 0L,
   @Expose
   nickName: String = "",
   password: String = "",
   @Expose
   name: String = "",
   @Expose
   surname: String = "",
   isMale:Boolean = false,
   isOnline:Boolean = false,
   status: String = "",
   latitude:Double = 0.0,
   longitude:Double = 0.0,
   @Expose
   imageId: Long = 0L,
   
) extends Serializable





class UserDAO(tag: Tag) extends Table[User](tag,"user"){
   
   def id = column[Long]("id",O.PrimaryKey,O.AutoInc,O.Unique)
   def nickName = column[String]("nickname",O.Unique)
   def password = column[String]("password")
   def name = column[String]("name")
   def surname = column[String]("surname")
   def isMale = column[Boolean]("is_male")
   def isOnline = column[Boolean]("is_online")
   def status = column[String]("status")
   def latitude = column[Double]("latitude")
   def longtitude = column[Double]("longtitude")
   def imageId = column[Long]("image_id")
   
   def * = (id,nickName,
      password,name,
      surname,isMale,
      isOnline,
      status,
      latitude,longtitude,
      imageId) <> (User.tupled,User.unapply)
   
}

case class UserUser(

   userId1:Long = 0L,
   userId2:Long = 0L

 )

class UserUserDAO(tag: Tag) extends Table[UserUser](tag,"user_user"){
   
   def userId1 = column[Long]("user_id_1")
   def userId2 = column[Long]("user_id_2")
   
   def * = (userId1,userId2) <> (UserUser.tupled,UserUser.unapply)
   
}


case class UserHipeImage(
                        userId:Long,
                        hipeImageId:Long
                        )

class UserHipeImageDAO(tag: Tag) extends Table[UserHipeImage](tag,"user_hipe_image"){
   
   def userId = column[Long]("user_id")
   def hipeImageId = column[Long]("hipe_image_id")
   
   def * = (userId,hipeImageId) <> (UserHipeImage.tupled,UserHipeImage.unapply)
   
}