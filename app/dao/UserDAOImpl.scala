package dao

import dao.sql.{Sql, UserSql}
import dao.traits.UserDAO
import javax.inject.Inject
import models.dto.SearchableUserForm
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserDAOImpl @Inject()(
                              protected val dbConfigProvider: DatabaseConfigProvider,
                              private val jwtCoder: JWTCoder)(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile]
      with UserDAO[Future] {

   override def getUsersByeventID(eventID: Long) = {
      db.run(UserSql.getUsersByeventIDJoinImage(eventID))
   }

   override def checkUserExistence(username: String) = {
      db.run(UserSql.checkUserExistence(username))
   }

   override def updateUser(user: User) = {
      db.run(UserSql.updateUser(user).map(_ => user))
   }

   override def clientUpdateUser(user: User) = {
      db.run(UserSql.clientUpdateUser(user).map(_ => user))
   }

   override def getFriends(userID: Int) = {
      db.run(UserSql.getFriends(userID))
   }

   override def getFriendsIds(userID: Int) = {
      db.run(UserSql.getFriendsIds(userID))
   }

   override def findUser(userID: Int, searchString: String) = {
      db.run(UserSql.findUser(userID, searchString))
   }

   override def getById(userID: Int) = {
      db.run(UserSql.getById(userID))
   }

   override def createUsersRelation(userToUser: UserToUserRelation) = {
      val query = UserSql
         .checkUserExistence(userToUser.userTo)
         .zip(UserSql.checkIsBlocked(userToUser.userFrom, userToUser.userTo))
         .flatMap {
            case (userExists, userIsBlockedBy) =>
               if (userExists && !userIsBlockedBy)
                  UserSql.createUserRelation(userToUser)
               else
                  Sql(0)
         }

      db.run(query)

   }

   override def removeUsersRelation(userToUser: UserToUserRelation) = {
      db.run(UserSql.removeUsersRelation(userToUser))
   }

   override def getByUsername(username: String) = {
      db.run(UserSql.getByUsername(username))
   }

   override def getTokenByuserID(username: String) = {
      db.run(UserSql.getTokenByUsername(username))
   }

   override def getusersIDssByeventID(eventID: Long) = {
      db.run(UserSql.getusersIDssByeventID(eventID))
   }

   override def searchUser(userID:Int, query: String) = {
      db.run(UserSql.searchUser(userID,query).map(_.map(SearchableUserForm.tupled)))
   }

}
