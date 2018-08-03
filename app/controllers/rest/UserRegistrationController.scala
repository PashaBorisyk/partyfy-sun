package controllers.rest

import db.services.interfaces.UserRegistrationService
import implicits.implicits._
import javax.inject.Inject
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile
import util.logger

import scala.concurrent.ExecutionContext

class UserRegistrationController @Inject()(
                                             protected val dbConfigProvider: DatabaseConfigProvider,
                                             protected val userRegistrationService: UserRegistrationService,
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
               Created(token.toJson)
         }.recover {
            case e: PSQLException =>
               logger.debug("Insert error:", e)
               Conflict
            case e: Exception =>
               e.printStackTrace()
               InternalServerError(e.getMessage)
         }
   }
   
   def registerUserStepTwo(publicToken: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userRegistrationService.registerUserStepThree(publicToken).collect {
   
            case
            entry:UserRegistrationService =>
               if (entry.confirmed)
                  return NotModified
               else if (entry.expirationDateMills > System.currentTimeMillis()) {
                  userRegistrationService.deleteUserRegistration(entry.id)
                  return Gone
               } else {
                    return Ok
               }
            case None =>
               return NotFound
         }.recover {
            case e: PSQLException =>
               logger.debug("Insert error:", e);
               return Conflict
            case e: Exception =>
               logger.error(e.getMessage)
               InternalServerError(e.getMessage)
         }
         
   }
   
   
}
