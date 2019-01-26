package db.services

import db.services.interfaces.UserRegistrationService
import javax.inject.Inject
import models.{User, UserDAO, UserRegistration, UserRegistrationDAO}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
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
   
   val userRegistrationTable = TableQuery[UserRegistrationDAO]
   val userTable = TableQuery[UserDAO]
   
   def registerUserStepOne(username: String, password: String) = {
      
      val jwtPublicTokenFirst = jwtCoder.encodePublic((username, password))
      
      val userRegistration = UserRegistration(
         username = username,
         password = password,
         publicTokenFirst = jwtPublicTokenFirst
      )
      db.run(userRegistrationTable += userRegistration).map {
         _ => userRegistration.publicTokenFirst
      }
   }
   
   def registerUserStepTwo(username: String, emailAddress: String, jwtPublicTokenFirst: String) = {
      db.run(userRegistrationTable.filter {
         entry =>
            entry.username === username &&
               entry.publicTokenFirst === jwtPublicTokenFirst &&
               entry.expirationDateMills > System.currentTimeMillis() &&
               entry.confirmed === false
         
      }.result.head).map {
         userRegistration =>
            
            val updatedUserRegistration = userRegistration.copy(
               publicTokenSecond = jwtCoder.encodePublic((username, emailAddress))
            )
            db.run(userRegistrationTable.update(updatedUserRegistration))
            updatedUserRegistration
      }
      
   }
   
   def registerUserStepThree(publicTokenTwo: String) = {
      
      db.run(userRegistrationTable.filter {
         entry =>
            entry.publicTokenTwo === publicTokenTwo
      }.result.head).map {
         userRegistration =>
            val newUser = User(
               username = userRegistration.username,
               isOnline = true
            )
            db.run((userTable returning userTable.map(_.id)) += newUser).map {
               id =>
                  val token = jwtCoder.encodePrivate((userRegistration.username, userRegistration.password, id))
                  eventMessagePublisherService ! userRegistration.copy(
                     id = id,
                     privateToken = token
                  )
            }
            userRegistration
      }
      
   }
   
   def deleteUserRegistration(registrationId: Long) = {
      db.run(userRegistrationTable.filter(_.id === registrationId).delete)
   }
   
}