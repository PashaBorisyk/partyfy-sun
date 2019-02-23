package controllers

import implicits.implicits._
import javax.inject.Inject
import models.persistient.UserRegistration
import org.postgresql.util.PSQLException
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.database.traits.UserRegistrationService
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

//noinspection TypeAnnotation
class UserRegistrationController @Inject()(
                                             protected val dbConfigProvider: DatabaseConfigProvider,
                                             protected val userRegistrationService: UserRegistrationService[Future],
                                             val jwtCoder: JWTCoder,
                                             cc: ControllerComponents)(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger(this.getClass)

   //todo usernameToken -> usernameEmailToken -> usernamePasswordId token. End of registration
   def registerUserStepOne(username: String, password: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userRegistrationService.registerUserStepOne(username, password).map {
            userRegistration =>
               if(userRegistration.duplicated){
                  Conflict
               } else {
                  logger.debug(s"Created with userRegistration : ${userRegistration.username}")
                  Created(userRegistration.publicTokenFirst)
               }
         }.recover {
            case e: PSQLException =>
               logger.debug("Insert error:", e)
               Conflict
            case e: Exception =>
               e.printStackTrace()
               InternalServerError(e.getMessage)
         }
   }
   
   def registerUserStepTwo(username: String, email: String, publicTokenFirst: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userRegistrationService.registerUserStepTwo(username, email, publicTokenFirst).collect {
            case Some(entry) =>
               if (entry.duplicated)
                  Conflict
               else if (entry.expirationDateMills <= System.currentTimeMillis()) {
                  userRegistrationService.deleteUserRegistration(entry.id)
                  Gone
               } else {
                  Ok(entry.publicTokenSecond)
               }
            case None =>
               NotFound
         }.recover {
            case e: PSQLException =>
               logger.debug("Insert error:", e)
               Conflict
            case e: Exception =>
               logger.error(e.getMessage)
               InternalServerError(e.getMessage)
         }
      
   }
   
   def registerUserStepThree(publicTokenTwo: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userRegistrationService.registerUserStepThree(publicTokenTwo).collect {
            case Some(userRegistration) =>
               if (userRegistration.duplicated){
                  userRegistrationService.deleteUserRegistration(userRegistration.id)
                  Conflict
               }
               else if (userRegistration.expirationDateMills <= System.currentTimeMillis() && !userRegistration.privateToken.notNullOrEmpty) {
                  logger.debug("UserRegistrationFound but is not active anymore")
                  userRegistrationService.deleteUserRegistration(userRegistration.id)
                  Gone
               } else {
                  userRegistrationService.deleteUserRegistration(userRegistration.id)
                  Ok(userRegistration.privateToken)
               }
            case None =>
               NotFound
         }.recover {
            case e: PSQLException =>
               logger.debug("Insert error:", e)
               Conflict
            case e: Exception =>
               logger.error(e.getMessage)
               InternalServerError(e.getMessage)
         }
      
   }
   
   
}
