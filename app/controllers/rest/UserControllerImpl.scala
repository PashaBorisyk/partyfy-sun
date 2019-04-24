package controllers.rest

import controllers.rest.implicits._
import javax.inject.{Inject, Singleton}
import models.persistient.User
import models.implicits._
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
   extends AbstractController(cc)
      with HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger("application")

   def checkUserExistence(username: String) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.checkUserExistence(username).map { userExists: Boolean =>
         if (userExists)
            Ok
         else
            NoContent
      }

   }

   def login(username: String, password: String) = Action.async { implicit req =>
      logger.debug(req.toString)
      userService.login(username, password).map {
         case Some(token) => Ok(Json.toJson(token))
         case None => NoContent
      }
   }

   def update() = Action.async(parse.json[User]) { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.clientUpdateUser(req.body).map { user =>
         Accepted(user.token)
      }

   }

   def find(query: String) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.findUser(query).map { usersWithImage =>
         if (usersWithImage.nonEmpty)
            Ok(Json.toJson(usersWithImage))
         else
            NoContent
      }
   }

   def createUsersRelation(userID: Int, relationType: String) = Action.async {
      implicit req =>
         logger.debug(req.toString)
         implicit val token = getToken
         userService.createUsersRelation(userID, relationType).map {
            insertedRows =>
               if (insertedRows > 0)
                  Ok
               else
                  NotModified
         }

   }

   def removeUsersRelation(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.removeUsersRelation(userID).map { rowsDeleted =>
         if (rowsDeleted > 0)
            Accepted
         else NotModified
      }

   }

   def getById(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.getById(userID).map { userWithImage =>
         Ok(Json.toJson(userWithImage))
      }

   }

   def getFriends(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.getFriends(userID).map { usersWithImages =>
         if (usersWithImages.nonEmpty)
            Ok(Json.toJson(usersWithImages))
         else
            NoContent
      }
   }

   def getFriendsIDs(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.getFriendsIds(userID).map { friendsIds =>
         if (friendsIds.nonEmpty)
            Ok(Json.toJson(friendsIds))
         else
            NoContent
      }
   }

   def getByEventID(eventID: Long) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      userService.getUsersByeventID(eventID).map { usersWithImages =>
         if (usersWithImages.nonEmpty)
            Ok(Json.toJson(usersWithImages))
         else
            NoContent
      }

   }

   def getIDsByEventID(eventID:Long) = Action.async{ implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      userService.getUsersIDsByEventID(eventID).map{ usersIDs =>
         if(usersIDs.nonEmpty)
            Ok(Json.toJson(usersIDs))
         else
            NoContent
      }
   }

   def searchUser(query:String) = Action.async{ implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      userService.searchUser(query).map{ userSearchableForms =>
         if (userSearchableForms.nonEmpty)
            Ok(Json.toJson(userSearchableForms))
         else
            NoContent

      }
   }


}
