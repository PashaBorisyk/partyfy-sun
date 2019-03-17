package services.database.traits

import com.google.inject.ImplementedBy
import models.persistient.{User, UserRegistration}
import services.database.UserRegistrationServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserRegistrationServiceImpl])
trait UserRegistrationService[T[_]] {

   def createRegistration(username: String, secret: String, email: String) : T[UserRegistration]

   def confirmRegistrationAndCreateUser(registrationToken:String): T[(UserRegistration,User)]

   def deleteUserRegistration(id: Long): T[Int]

}
