package services.database.traits

import com.google.inject.ImplementedBy
import models.UserRegistration
import services.database.UserRegistrationServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserRegistrationServiceImpl])
trait UserRegistrationService[T[_]] {

   def registerUserStepOne(username: String, password: String): T[String]

   def registerUserStepTwo(username: String, emailAddress: String, jwtPublicTokenFirst: String)
   : T[Option[UserRegistration]]

   def registerUserStepThree(publicToken: String): T[Option[UserRegistration]]

   def deleteUserRegistration(id: Long): T[Int]

}
