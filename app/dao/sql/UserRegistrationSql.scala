package dao.sql

import dao.sql.tables.UserRegistrationTable
import dao.sql.tables.implicits._
import models.persistient.{UserRegistration, UserRegistrationState}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext


private[dao] object UserRegistrationSql extends Sql {

   private val userRegistrationTable = TableQuery[UserRegistrationTable]

   def create(userRegistration: UserRegistration)(implicit ec: ExecutionContext) = {
      (userRegistrationTable returning userRegistrationTable.map(_.id) += userRegistration).map { userRegistrationId =>
         userRegistration.copy(id = userRegistrationId)
      }
   }

   def existsWithRegistrationToken(registrationToken: String) = {
      userRegistrationTable.filter {
         user =>
            user.registrationToken === registrationToken
      }.exists.result
   }

   def findByRegistrationToken(registrationToken: String) = {
      userRegistrationTable.filter {
         user =>
            user.registrationToken === registrationToken
      }.result.headOption
   }

   def update(userRegistration: UserRegistration)(implicit ec: ExecutionContext) = {
      userRegistrationTable.update(userRegistration).map { _ =>
         userRegistration
      }
   }

   def getByUsernameAndPublicTokenFirst(username: String, publicTokenFirst: String) = {
      userRegistrationTable.filter {
         userRegistration =>
            (userRegistration.username === username) &&
               (userRegistration.registrationToken === publicTokenFirst)
      }.result.headOption
   }

   def deleteUserRegistration(registrationId: Long) = {
      userRegistrationTable.filter(_.id === registrationId).delete
   }

}
