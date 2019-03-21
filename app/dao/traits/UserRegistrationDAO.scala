package dao.traits

import com.google.inject.ImplementedBy
import dao.UserRegistrationDAOImpl
import models.persistient.{User, UserRegistration}

import scala.language.higherKinds

@ImplementedBy(classOf[UserRegistrationDAOImpl])
trait UserRegistrationDAO[T[_]] {

   def createUserRegistration(userRegistration: UserRegistration) : T[UserRegistration]

   def confirmRegistrationAndGetUser(userRegistration: UserRegistration, tokenGen : Long => String)
   : T[(UserRegistration,User)]

   def deleteUserRegistration(id: Long): T[Int]

}
