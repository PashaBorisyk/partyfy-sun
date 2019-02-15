package controllers

import implicits.implicits._
import javax.inject.Inject
import models.UserRegistration
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.database.traits.UserRegistrationService
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile
import util.logger

import scala.concurrent.{ExecutionContext, Future}

//noinspection TypeAnnotation
class UserRegistrationController @Inject()(
                                             protected val dbConfigProvider: DatabaseConfigProvider,
                                             protected val userRegistrationService: UserRegistrationService[Future],
                                             val jwtCoder: JWTCoder,
                                             cc: ControllerComponents)(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
   
   //todo usernameToken -> usernameEmailToken -> usernamePasswordId token. End of registration
   def registerUserStepOne(username: String, password: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userRegistrationService.registerUserStepOne(username, password).map {
            token =>
               logger.debug(s"Created with token : $token")
               Created(token)
         }.recover {
            case e: PSQLException =>
               logger.debug("Insert error:", e)
               Conflict
            case e: Exception =>
               e.printStackTrace()
               InternalServerError(e.getMessage)
         }
   }
   
   def registerUserStepTwo(username: String, email: String, publicToken: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userRegistrationService.registerUserStepTwo(username, email, publicToken).collect {
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
            case Some(entry) =>
               if (entry.duplicated){
                  userRegistrationService.deleteUserRegistration(entry.id)
                  Conflict
               }
               else if (entry.expirationDateMills <= System.currentTimeMillis() && !entry.privateToken.notNullOrEmpty) {
                  logger.debug("UserRegistrationFound but is not active anymore")
                  userRegistrationService.deleteUserRegistration(entry.id)
                  Gone
               } else {
                  userRegistrationService.deleteUserRegistration(entry.id)
                  Ok(entry.privateToken)
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
