package controllers.rest

import db.services.UserServiceImpl
import implicits.implicits._
import javax.inject.{Inject, Singleton}
import org.postgresql.util.PSQLException
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile
import util._

import scala.concurrent.ExecutionContext


@Singleton
class UserControllerImpl @Inject()(
                                     protected val dbConfigProvider: DatabaseConfigProvider,
                                     protected val userService: UserServiceImpl,
                                     val jwtCoder: JWTCoder,
                                     cc: ControllerComponents)(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
   
   def checkUserExistence(username: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.checkUserExistence(username).map {
            s: Boolean => if (s) Found(s"User with nickname: $username already exists") else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
      
   }
   //todo usernameToken -> usernameEmailToken -> usernamePasswordId token. End of registration
   def registerUserStepOne(username: String, password: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.registerUserStepOne(username, password).map {
            token => logger.debug(s"Created with token : $token"); Created(token.toJson)
         }.recover {
            case e: PSQLException => logger.debug("Insert error:", e); Conflict
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
   }
   
   def registerUserStepTwo(publicToken: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.registerUserStepTwo(publicToken).map {
            case Some(entry) =>
               if (entry.confirmed)
                  NotModified
               else if (entry.expirationDateMills > System.currentTimeMillis()) {
                  userService.deleteUserRegistration(entry.id)
                  Gone
               } else {
                  userService.
                  Ok
               }
            case None =>
               NotFound
         }.recover {
            case e: PSQLException => logger.debug("Insert error:", e); Conflict
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
   }
   
   def updateUser() = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.updateUser(req.body).map {
            id: Int => Created(id.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
      
   }
   
   def findUser(requesterUserId: Long, query: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.findUser(requesterUserId, query).map { s =>
            if (s.nonEmpty)
               Ok(s.toArray.toJson)
            else
               NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
   }
   
   def addUserToFriends(userId: Long, advancedUserId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.addUserToFriends(userId, advancedUserId).map {
            _ => Ok(advancedUserId.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
      
   }
   
   def removeUserFromFriends(userId: Long, advancedUserId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.removeUserFromFriends(userId, advancedUserId).map {
            _ => Accepted(advancedUserId.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
      
   }
   
   def getById(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getById(userId).map {
            s => Ok(s.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
      
   }
   
   def getFriends(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getFriends(userId).map {
            s => Ok(s.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
   }
   
   def getFriendsIds(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getFriendsIds(userId).map {
            s => Ok(s.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
   }
   
   def getUsersByEvent(eventId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getUsersByEventId(eventId).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace(); e.getMessage
            })
         }
      
   }
   
   def loginUser(username: String, password: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.login(username, password).map {
            token =>
               Ok(token.toJson)
         }.recover {
            case _: NoSuchElementException =>
               NotFound
            case error => logger.debug("Error while loging user : ", error)
               InternalServerError(error.toJson)
         }
   }
   
}