package controllers.rest

import db.services.UserServiceImpl
import implicits.implicits._
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile
import util._
import play.api.libs.json.Json
import io.jsonwebtoken.Jwt._



import scala.concurrent.ExecutionContext


@Singleton
class UserControllerImpl @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider,
                                    protected val userService: UserServiceImpl,
                                    cc: ControllerComponents)(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
   
   def checkUserExistence(nickName: String) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.checkUserExistence(nickName).map {
            s: Boolean => if (s) Found(s"User with nickname: $nickName already exists") else NoContent
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def registerUser = Action.async {
      req =>
         logger.debug(req.toString)
         userService.registerUser(req.body).map {
            id: Long => Created(id.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def updateUser() = Action.async {
      req =>
         logger.debug(req.toString)
         userService.updateUser(req.body).map {
            id: Int => Created(id.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def findUser(requesterUserId: Long, query: String) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.findUser(requesterUserId, query).map { s =>
            if(s.nonEmpty)
               Ok(s.toArray.toJson)
            else
               NoContent
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
   def addUserToFriends(userId: Long, advancedUserId: Long) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.addUserToFriends(userId, advancedUserId).map {
            _ => Ok(advancedUserId.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def removeUserFromFriends(userId: Long, advancedUserId: Long) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.removeUserFromFriends(userId, advancedUserId).map {
            _ => Accepted(advancedUserId.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def getById(userId: Long) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.getById(userId).map {
            s => Ok(s.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def getFriends(userId: Long) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.getFriends(userId).map {
            s => Ok(s.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
   def getFriendsIds(userId: Long) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.getFriendsIds(userId).map {
            s => Ok(s.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
   def getUsersByEvent(eventId: Long) = Action.async {
      req =>
         logger.debug(req.toString)
         userService.getUsersByEventId(eventId).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def loginUser(nickName: String, password: String) = Action.async{
      req =>
         val payLoad = Json.obj("nickname"->nickName,"password"->password)
         val  jwt = 
   
   }
   
}