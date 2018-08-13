package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.UserRegistrationServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[UserRegistrationServiceImpl])
trait UserRegistrationService {
   
   def registerUserStepOne(username:String,password:String):Future[Any]
   def registerUserStepTwo(username:String,emailAddress:String,jwtPublicTokenFirst:String):Future[Any]
   def registerUserStepThree(publicToken:String):Future[Any]
   def deleteUserRegistration(id:Long):Future[Any]
   
}
