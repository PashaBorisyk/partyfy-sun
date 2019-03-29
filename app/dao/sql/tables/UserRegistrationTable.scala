package dao.sql.tables

import dao.sql.tables.implicits._
import models.persistient.{UserRegistration, UserRegistrationState}
import slick.jdbc.PostgresProfile.api._

private[sql] class UserRegistrationTable(tag: Tag)
   extends Table[UserRegistration](tag, "user_registration") {

   def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)

   def username = column[String]("username", O.Unique)

   def emailAddress = column[String]("email_address", O.Unique)

   def registrationToken = column[String]("registration_token", O.Unique)

   def state = column[UserRegistrationState]("state")

   def expirationDateMills = column[Long]("expiration_date_mills")

   def * =
      (
         id,
         username,
         emailAddress,
         registrationToken,
         state,
         expirationDateMills
      ) <> (UserRegistration.tupled, UserRegistration.unapply)

}
