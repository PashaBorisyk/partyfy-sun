package services.database.traits

import com.google.inject.ImplementedBy
import models.persistient.{User, UserRegistration}
import services.database.UserRegistrationServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserRegistrationServiceImpl])
trait UserRegistrationService[T[_]] {

   def registerUser(username: String, password: String, email: String) : T[UserRegistration]

   def confirmRegistrationAndCreateUser(registrationToken:String): T[UserRegistration]

   def deleteUserRegistration(id: Long): T[Int]

}
