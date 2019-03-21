package controllers.rest

import controllers.rest.implicits._
import javax.inject.{Inject, Singleton}
import models.persistient.User
import models.persistient.implicits._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import services.database.UserServiceImpl
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class UserControllerImpl @Inject()(
                                     protected val dbConfigProvider: DatabaseConfigProvider,
                                     protected val userService: UserServiceImpl,
                                     cc: ControllerComponents)(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger(this.getClass)

   def checkUserExistence(username: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.checkUserExistence(username).map {
            userExists: Boolean =>
               if (userExists)
                  Ok else
                  NoContent
         }

   }

   def updateUser() = Action.async(parse.json[User]) {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.clientUpdateUser(req.body).map {
            user => Accepted(user.token)
         }

   }

   def findUser(query: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.findUser(query).map { usersWithImage =>
            if (usersWithImage.nonEmpty)
               Ok(Json.toJson(usersWithImage))
            else
               NoContent
         }
   }

   def createUsersRelation(userId: Long,relationType:String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.createUsersRelation(userId,relationType).map {
            insertedRows =>
               if (insertedRows > 0)
                  Ok
               else
                  NotModified
         }

   }

   def removeUsersRelation(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.removeUsersRelation(userId).map {
            rowsDeleted =>
               if (rowsDeleted > 0)
                  Accepted
               else NotModified
         }

   }

   def getById(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.getById(userId).map { userWithImage =>
            Ok(Json.toJson(userWithImage))
         }

   }

   def getFriends(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.getFriends(userId).map {
            usersWithImages =>
               if (usersWithImages.nonEmpty)
                  Ok(Json.toJson(usersWithImages))
               else
                  NoContent
         }
   }

   def getFriendsIds(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.getFriendsIds(userId).map {
            friendsIds =>
               if (friendsIds.nonEmpty)
                  Ok(Json.toJson(friendsIds))
               else
                  NoContent
         }
   }

   def getUsersByEvent(eventId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.getUsersByEventId(eventId).map {
            usersWithImages =>
               if (usersWithImages.nonEmpty)
                  Ok(Json.toJson(usersWithImages))
               else
                  NoContent
         }

   }

   def login(username: String, password: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         userService.login(username, password).map {
            case Some(token) => Ok(token)
            case None => NoContent
         }
   }

}