package controllers

import implicits.implicits._
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import services.database.UserServiceImpl
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile
import util._

import scala.concurrent.ExecutionContext


@Singleton
class UserControllerImpl @Inject()(
                                     protected val dbConfigProvider: DatabaseConfigProvider,
                                     protected val userService: UserServiceImpl,
                                     cc: ControllerComponents)(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

   def checkUserExistence(username: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.checkUserExistence(username).map {
            userExists: Boolean => if (userExists)
               Ok else
               NoContent
         }.recover {
            case e: Exception =>
               logger.debug("Error while checkUserExistence : ", e)
               InternalServerError(e.getMessage)
         }

   }

   def updateUser() = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.updateUser(req.body, getToken).map {
            token: String => Accepted(token.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while updateUser : ", e)
               InternalServerError(e.getMessage)
         }

   }

   def findUser(query: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.findUser(query, getToken).map { s =>
            if (s.nonEmpty)
               Ok(s.toArray.toJson)
            else
               NoContent
         }.recover {
            case e: Exception =>
               logger.debug("Error while findUser : ", e)
               InternalServerError(e.getMessage)
         }
   }

   def addUserToFriends(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.addUserToFriends(userId, getToken).map {
            _ => Ok(userId.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while addUserToFriends : ", e)
               InternalServerError(e.getMessage)
         }

   }

   def removeUserFromFriends(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.removeUserFromFriends(userId, getToken).map {
            _ => Accepted(userId.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while removeUserFromFriends : ", e)
               InternalServerError(e.getMessage)
         }

   }

   def getById(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getById(userId, getToken).map {
            s => Ok(s.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while getById : ", e)
               InternalServerError(e.getMessage)
         }

   }

   def getFriends(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getFriends(userId, getToken).map {
            s => Ok(s.toArray.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while getFriends : ", e)
               InternalServerError(e.getMessage)
         }
   }

   def getFriendsIds(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getFriendsIds(userId, getToken).map {
            s => Ok(s.toArray.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while getFriendsIds : ", e)
               InternalServerError(e.getMessage)
         }
   }

   def getUsersByEvent(eventId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.getUsersByEventId(eventId, getToken).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error while getUsersByEvent : ", e)
               InternalServerError(e.getMessage)
         }

   }

   def loginUser(username: String, password: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.login(username, password).map {
            case Some(token) => Ok(token)
            case None => NoContent
         }.recover {
            case e:Exception =>
               logger.debug("Error while loginUser : ", e)
               InternalServerError(e.toJson)
         }
   }

}