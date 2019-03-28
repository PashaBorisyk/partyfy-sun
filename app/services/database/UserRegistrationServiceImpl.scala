package services.database

import dao.traits.{UserDAO, UserRegistrationDAO}
import javax.inject.Inject
import models.TokenRepRegistration
import models.persistient.UserRegistration
import services.database.traits.UserRegistrationService
import services.traits.JWTCoder

import scala.concurrent.{ExecutionContext, Future}

class UserRegistrationServiceImpl @Inject()(private val jwtCoder: JWTCoder,
                                            private val userRegistrationDAO: UserRegistrationDAO[Future],
                                            private val userDAO:UserDAO[Future])
                                           (implicit ec:ExecutionContext)
   extends UserRegistrationService[Future] {

   def createRegistration(username: String, secret: String, email: String) = {
      val registrationTokenRep = TokenRepRegistration(username,secret,email)
      val registrationToken = jwtCoder.encode(registrationTokenRep)
      val userRegistration = UserRegistration(
         registrationToken = registrationToken,
         emailAddress = email,
         username = username
      )
      userRegistrationDAO.createUserRegistration(userRegistration)
   }

   def confirmRegistrationAndCreateUser(registrationToken: String) = {
      val registrationTokenRep = jwtCoder.decodeRegistrationToken(registrationToken)
      val userRegistration = UserRegistration(username = registrationTokenRep.username,registrationToken = registrationToken)
      val tokenGen = jwtCoder.fromLazyUserId(registrationTokenRep)(_)
      userRegistrationDAO.confirmRegistrationAndGetUser(userRegistration,tokenGen).map{
         case (registration,user)=>
            registration->user
      }
   }

   def deleteUserRegistration(registrationId: Long) = {
      userRegistrationDAO.deleteUserRegistration(registrationId)
   }

}