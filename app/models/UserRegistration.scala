package models

import java.io.Serializable

import slick.jdbc.PostgresProfile.api._

case class UserRegistration(
                  id: Long = 0L,
                  username: String = "",
                  password:String = "",
                  emailAddress:String = "",
                  publicToken: String = "",
                  privateToken:String = "",
                  confirmed: Boolean = false,
                  expirationDateMills:Long = System.currentTimeMillis() + UserRegistration.REGISTRATION_TIME_ATTEMPT

) extends Serializable

object UserRegistration extends Serializable{
   final val REGISTRATION_TIME_ATTEMPT = 3600*1000*2
}

class UserRegistrationDAO(tag: Tag) extends Table[UserRegistration](tag, "user_registration") {
   
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
   
   def username = column[String]("username", O.Unique)
   
   def password = column[String]("password",O.Unique)
   
   def emailAddress = column[String]("email_address",O.Unique)
   
   def publicToken = column[String]("public_token",O.Unique)
   
   def privateToken = column[String]("private_token",O.Unique)
   
   def confirmed = column[Boolean]("is_confirmed")
   
   def expirationDateMills = column[Long]("expiration_date_mills")
   
   def * = (
      id, username,
      password,
      emailAddress , publicToken,
      privateToken,confirmed,
      expirationDateMills
   ) <> (UserRegistration.tupled, UserRegistration.unapply)
   
}