package controllers.rest

import controllers.rest.implicits.getToken
import javax.inject.Inject
import models.persistient.UserRegistrationState
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
                                             cc: ControllerComponents)(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger(this.getClass)

   //todo usernameToken -> usernameEmailToken -> usernamePasswordId token. End of registration
   def registerUser(username: String, secret: String, email: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userRegistrationService.registerUser(username, secret,email).map {
            userRegistration =>
               if(userRegistration.state == UserRegistrationState.DUPLICATE){
                  Conflict
               } else {
                  logger.debug(s"Created with userRegistration : ${userRegistration.username}")
                  Created(userRegistration.registrationToken)
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
   
   def confirmRegistrationAndCreateUser(registrationToken: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userRegistrationService.confirmRegistrationAndCreateUser(registrationToken).map { registration =>
               if (registration.state == UserRegistrationState.EXPIRED) {
                  Gone
               } else if (registration.state == UserRegistrationState.EXPIRED){
                  Ok("")
               } else {
                  InternalServerError("Unknown registration state")
               }
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
