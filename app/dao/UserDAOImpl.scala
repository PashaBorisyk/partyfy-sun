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

   def getUsersByEventId(eventId: Long): Future[Seq[(User, Option[Image])]] = {
      db.run(UserSql.getUsersByEventId(eventId))
   }

   def checkUserExistence(username: String) = {
      db.run(UserSql.checkUserExistence(username))
   }

   def updateUser(user: User): Future[User] = {
      db.run(UserSql.updateUser(user))
   }

   def getFriends(userId: Long): Future[Seq[(User, Option[Image])]] = {
      db.run(UserSql.getFriends(userId))
   }

   def getFriendsIds(userId: Long): Future[Seq[Long]] = {
      db.run(UserSql.getFriendsIds(userId))
   }

   def findUser(userId: Long, searchString: String): Future[Seq[(User, Option[Image])]] = {
      db.run(UserSql.findUser(userId, searchString))
   }

   def getById(id: Long): Future[(User, Option[Image])] = {
      db.run(UserSql.getById(id))
   }

   def addUserToFriends(userId: Long, addedUserId: Long) = {
      db.run(UserSql.addUserToFriends(userId, addedUserId))
   }

   def removeUserFromFriends(userId: Long, removedUserId: Long) = {
      db.run(UserSql.removeUserFromFriends(userId, removedUserId))
   }

   def getByUsername(username: String): Future[Option[User]] = {
      db.run(UserSql.getByUsername(username))
   }

   override def getTokenByUserId(username: String) = {
      db.run(UserSql.getTokenByUsername(username))
   }

}