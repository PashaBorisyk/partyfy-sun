package controllers.rest

import javax.inject.Inject
import models.persistient.UserRegistrationState
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
   extends AbstractController(cc)
      with HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger("application")

   //todo usernameToken -> usernameEmailToken -> usernamePasswordId token. End of registration
   def createRegistration(username: String, secret: String, email: String) =
      Action.async { implicit req =>
         logger.debug(req.toString)
         userRegistrationService.createRegistration(username, secret, email).map {
            userRegistration =>
               if (userRegistration.state == UserRegistrationState.DUPLICATE) {
                  Conflict
               } else {
                  logger.debug(
                     s"Created with userRegistration : ${userRegistration.username}")
                  Created(userRegistration.registrationToken)
               }
         }
      }

   def confirmRegistrationAndCreateUser(registrationToken: String) =
      Action.async { implicit req =>
         logger.debug(req.toString)
         userRegistrationService
            .confirmRegistrationAndCreateUser(registrationToken)
            .map {
               case (registration, user) =>
                  if (registration.state == UserRegistrationState.EXPIRED) {
                     Gone
                  } else if (registration.state == UserRegistrationState.CONFIRMED) {
                     Ok(user.token)
                  } else {
                     InternalServerError(
                        s"Illegal registration state : ${registration.state}")
                  }
            }

      }

}
