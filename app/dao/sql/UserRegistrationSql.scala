package dao.sql

import dao.sql.tables.UserRegistrationTable
import models.persistient.UserRegistration
import slick.jdbc.PostgresProfile.api._

private[dao] object UserRegistrationSql extends Sql {

   private val userRegistrationTable = TableQuery[UserRegistrationTable]

   def create(userRegistration: UserRegistration) = {
      (userRegistrationTable returning userRegistrationTable.map(_.id)
         into ((registration, id) => registration.copy(id))) += userRegistration
   }

   def existsWithRegistrationToken(registrationToken: String) = {
      _existsWithRegistrationToken(registrationToken).result
   }

   private val _existsWithRegistrationToken = Compiled {
      registrationToken: Rep[String] =>
         userRegistrationTable.filter { user =>
            user.registrationToken === registrationToken
         }.exists
   }

   def findByRegistrationToken(registrationToken: String) = {
      _getByRegistrationToken(registrationToken).result.headOption
   }

   private val _getByRegistrationToken = Compiled {
      registrationToken: Rep[String] =>
         userRegistrationTable.filter { user =>
            user.registrationToken === registrationToken
         }
   }

   def update(userRegistration: UserRegistration) = {
      userRegistrationTable.insertOrUpdate(userRegistration)
   }

   def getByUsernameAndPublicTokenFirst(username: String,
                                        registrationToken: String) = {
      _getByUsernameAndPublicTokenFirst(username, registrationToken).result.headOption
   }

   private val _getByUsernameAndPublicTokenFirst = Compiled {
      (username: Rep[String], registrationToken: Rep[String]) =>
         userRegistrationTable.filter { userRegistration =>
            (userRegistration.username === username) &&
               (userRegistration.registrationToken === registrationToken)
         }
   }

   def deleteUserRegistration(registrationId: Long) = {
      _getById(registrationId).delete
   }

   private val _getById = Compiled { registrationId: Rep[Long] =>
      userRegistrationTable.filter(_.id === registrationId)
   }

}
