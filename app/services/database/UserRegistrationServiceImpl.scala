package services.database

import javax.inject.Inject
import models.persistient.UserRegistration
import models.persistient.{User, UserDAO, UserRegistration, UserRegistrationDAO}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.database.traits.UserRegistrationService
import services.traits.{EventMessagePublisherService, JWTCoder}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserRegistrationServiceImpl @Inject()(
                                              protected val dbConfigProvider: DatabaseConfigProvider,
                                              private val jwtCoder: JWTCoder,
                                              private val eventMessagePublisherService: EventMessagePublisherService
                                           )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserRegistrationService[Future] {

   private val userRegistrationTable = TableQuery[UserRegistrationDAO]
   private val userTable = TableQuery[UserDAO]

   def registerUserStepOne(username: String, password: String) = {

      val jwtPublicTokenFirst = jwtCoder.encodePublic((username, password))

      val userRegistration = UserRegistration(
         username = username,
         secret = password,
         publicTokenFirst = jwtPublicTokenFirst
      )
      val execute = userTable.filter(_.username === username).exists.result.flatMap { userExists =>
         if (userExists) {
            SimpleDBIO(_ => userRegistration.copy(duplicated = true))
         } else {
            userRegistrationTable.filter {
               user =>
                  user.username === username && user.secret === password
            }.result.headOption.flatMap {
               case Some(result) =>
                  SimpleDBIO(_ => result)
               case None =>
                  (userRegistrationTable += userRegistration).map {
                     _ => userRegistration
                  }
            }
         }
      }
      db.run(execute)
   }

   def registerUserStepTwo(username: String, emailAddress: String, jwtPublicTokenFirst: String) = {
      val execute = userTable.filter(_.username === username).exists.result.flatMap { userExists =>
         if (!userExists) {
            userRegistrationTable.filter {
               entry =>
                  entry.username === username &&
                     entry.publicTokenFirst === jwtPublicTokenFirst &&
                     entry.isDuplicated === false &&
                     entry.isConfirmed === false

            }.result.headOption.flatMap {
               case Some(userRegistration) =>

                  val updatedUserRegistration = userRegistration.copy(
                     publicTokenSecond = jwtCoder.encodePublic((username, emailAddress)),
                     emailAddress = emailAddress
                  )
                  userRegistrationTable.update(updatedUserRegistration).map { _ =>
                     Some(updatedUserRegistration)
                  }
               case None => SimpleDBIO(_ => None)

            }

         } else {
            val duplicatedUserRegistration = UserRegistration(
               duplicated = true
            )
            userRegistrationTable.filter(_.username === username).delete.map { _ =>
               Some(duplicatedUserRegistration)
            }
         }

      }
      db.run(execute)
   }

   def registerUserStepThree(jwtPublicTokenTwo: String) = {

      val execute = userRegistrationTable.filter {
         _.publicTokenTwo === jwtPublicTokenTwo
      }.result.headOption.flatMap {

         case Some(userRegistration) =>
            userTable.filter {
               _.username === userRegistration.username
            }.map(_.id).exists.result.flatMap { exists =>

               if (exists) {
                  val duplicatedUserRegistration = userRegistration.copy(
                     duplicated = true,
                     secret = ""
                  )
                  userRegistrationTable.filter(_.username === userRegistration.username).delete.map { _ =>
                     Some(duplicatedUserRegistration)
                  }

               } else if (userRegistration.expirationDateMills >= System.currentTimeMillis()) {

                  val newUser = User(
                     username = userRegistration.username,
                     isOnline = true,
                     secret = userRegistration.publicTokenSecond,
                     email = userRegistration.emailAddress
                  )
                  (userTable returning userTable.map(_.id) += newUser).flatMap {
                     userId =>
                        val privateToken = jwtCoder.encodePrivate(userId, userRegistration.username, userRegistration.secret)
                        val lastUserRegistrationStep = userRegistration.copy(
                           userId = userId,
                           privateToken = privateToken,
                           confirmed = true,
                           secret = ""
                        )
                        eventMessagePublisherService ! lastUserRegistrationStep
                        userRegistrationTable.update(lastUserRegistrationStep).flatMap { _ =>
                           userTable.update(newUser.copy(
                              id = userId,
                              secret = privateToken
                           )).map { _ =>
                              Some(lastUserRegistrationStep)
                           }
                        }
                  }
               } else {
                  SimpleDBIO(_ => Some(userRegistration))
               }
            }
         case None => SimpleDBIO(_ => None)
      }

      db.run(execute)

   }

   def deleteUserRegistration(registrationId: Long) = {
      db.run(userRegistrationTable.filter(_.id === registrationId).delete)
   }

}