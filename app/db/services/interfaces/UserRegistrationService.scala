package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.UserRegistrationServiceImpl
import models.UserRegistration

@ImplementedBy(classOf[UserRegistrationServiceImpl])
trait UserRegistrationService[T[_]] {

   def registerUserStepOne(username: String, password: String): T[String]

   def registerUserStepTwo(username: String, emailAddress: String, jwtPublicTokenFirst: String): T[UserRegistration]

   def registerUserStepThree(publicToken: String): T[UserRegistration]

   def deleteUserRegistration(id: Long): T[Int]
   
}
