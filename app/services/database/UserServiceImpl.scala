package services.database

import implicits._
import dao.traits.UserDAO
import javax.inject.Inject
import models.TokenRepPrivate
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.database.traits.UserService
import services.traits.{JWTCoder}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  private val userDAO: UserDAO[Future],
                                  private val jwtCoder: JWTCoder
                               )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserService[Future] {

   def getUsersByEventId(eventId: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getUsersByEventId(eventId)
   }

   def checkUserExistence(username: String) = {
      userDAO.checkUserExistence(username)
   }

   def updateUser(user: User)(implicit token: TokenRepPrivate) = {
      userDAO.updateUser(user)
   }

   def getFriends(userId: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getFriends(userId)
   }

   def getFriendsIds(userId: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getFriendsIds(userId)
   }

   def findUser(searchString: String)(implicit token: TokenRepPrivate) = {
      userDAO.findUser(token.userId,searchString)
   }

   def getById(id: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getById(id)
   }

   def addUserToFriends(userId: Long)(implicit token: TokenRepPrivate) = {
      userDAO.addUserToFriends(token.userId,userId)
   }

   def removeUserFromFriends(userId: Long)(implicit token: TokenRepPrivate) = {
      userDAO.removeUserFromFriends(token.userId,userId)
   }

   def login(username: String, password: String) = {
      userDAO.getTokenByUserId(username).map{
         case Some(token) if jwtCoder.decodePrivateToken(token).secret == password => Some(token)
         case _ => None
      }
   }

}