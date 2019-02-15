package models

import java.io.Serializable

import com.google.gson.annotations.Expose
import slick.jdbc.PostgresProfile.api._

import scala.annotation.meta.field

private object Const {
   //2 hours
   final val REGISTRATION_TIME_ATTEMPT = 720000000L
}

case class UserRegistration(
                              id: Long = 0L,
                              @(Expose@field)
                              userId: Long = 0L,
                              username: String = "",
                              secret: String = "",
                              emailAddress: String = "",
                              publicTokenFirst: String = "",
                              publicTokenSecond: String = "",
                              @(Expose@field)
                              privateToken: String = "",
                              duplicated: Boolean = false,
                              confirmed:Boolean = false,
                              expirationDateMills: Long = System.currentTimeMillis() + Const.REGISTRATION_TIME_ATTEMPT

                           ) extends Serializable

class UserRegistrationDAO(tag: Tag) extends Table[UserRegistration](tag, "user_registration") {

   def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)

   def userId = column[Long]("user_id", O.AutoInc, O.Unique)

   def username = column[String]("username", O.Unique)

   def secret = column[String]("secret")

   def emailAddress = column[String]("email_address", O.Unique)

   def publicTokenFirst = column[String]("public_token_first", O.Unique)

   def publicTokenTwo = column[String]("public_token_two", O.Unique)

   def privateToken = column[String]("private_token", O.Unique)

   def isDuplicated = column[Boolean]("is_duplicated")

   def isConfirmed = column[Boolean]("is_confirmed")

   def expirationDateMills = column[Long]("expiration_date_mills")

   def * = (
      id, userId, username,
      secret, emailAddress,
      publicTokenFirst, publicTokenTwo,
      privateToken, isDuplicated,
      isConfirmed,expirationDateMills
   ) <> (UserRegistration.tupled, UserRegistration.unapply)

}