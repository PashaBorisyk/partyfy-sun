package dao

import dao.sql.UserSql
import dao.traits.UserDAO
import javax.inject.Inject
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                            private val jwtCoder: JWTCoder
                           )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserDAO[Future] {

   override def getUsersByEventId(eventId: Long) = {
      db.run(UserSql.getUsersByEventId(eventId))
   }

   override def checkUserExistence(username: String) = {
      db.run(UserSql.checkUserExistence(username))
   }

   override def updateUser(user: User) = {
      db.run(UserSql.updateUser(user))
   }

   override def clientUpdateUser(user: User) = {
      db.run(UserSql.clientUpdateUser(user))
   }

   override def getFriends(userId: Long) = {
      db.run(UserSql.getFriends(userId))
   }

   override def getFriendsIds(userId: Long) = {
      db.run(UserSql.getFriendsIds(userId))
   }

   override def findUser(userId: Long, searchString: String) = {
      db.run(UserSql.findUser(userId, searchString))
   }

   override def getById(id: Long) = {
      db.run(UserSql.getById(id))
   }

   override def addUserToFriends(userId: Long, addedUserId: Long) = {
      db.run(UserSql.addUserToFriends(userId, addedUserId))
   }

   override def removeUserFromFriends(userId: Long, removedUserId: Long) = {
      db.run(UserSql.removeUserFromFriends(userId, removedUserId))
   }

   override def getByUsername(username: String) = {
      db.run(UserSql.getByUsername(username))
   }

   override def getTokenByUserId(username: String) = {
      db.run(UserSql.getTokenByUsername(username))
   }

}