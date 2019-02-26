package dao.traits

import com.google.inject.ImplementedBy
import dao.UserRegistrationDAOImpl
import models.persistient.UserRegistration

@ImplementedBy(classOf[UserRegistrationDAOImpl])
trait UserRegistrationDAO[T[_]] {

   def registerUserStepOne(username: String, password: String): T[UserRegistration]

   def registerUserStepTwo(username: String, emailAddress: String, jwtPublicTokenFirst: String)
   : T[Option[UserRegistration]]

   def registerUserStepThree(jwtPublicTokenTwo: String): T[Option[UserRegistration]]

   def deleteUserRegistration(id: Long): T[Int]

}
