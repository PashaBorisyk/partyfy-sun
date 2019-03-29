package dao.sql

import dao.sql.implicits._
import dao.sql.tables.implicits._
import dao.sql.tables.{
   EventToUserTable,
   ImageTable,
   UserTable,
   UserToUserRelationTable
}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

private[dao] object UserSql {

   private val eventToUserTable = TableQuery[EventToUserTable]
   private val userTable = TableQuery[UserTable]
   private val userToUserRelationTable = TableQuery[UserToUserRelationTable]
   private val imageTable = TableQuery[ImageTable]

   def getUsersByEventIdJoinImage(eventId: Long) = {
      _getUsersByEventIdJoinImage(eventId).result
   }

   private val _getUsersByEventIdJoinImage = Compiled { eventId: Rep[Long] =>
      (userTable joinLeft imageTable on (_.imageId === _.id)).filter {
         case (user, _) =>
            (user.state === UserState.ACTIVE) && (user.id in eventToUserTable
               .filter { eventToUser =>
                  eventToUser.eventId === eventId
               }
               .map(_.userId))
      }
   }

   def insertUser(user: User) = {
      (userTable returning userTable.map(_.id) into ((user, id) => user.copy(id))) += user
   }

   def checkUserExistence(username: String) = {
      _checkUserExistence(username).result
   }

   private val _checkUserExistence = Compiled { username: Rep[String] =>
      userTable
         .filter { user =>
            user.username === username
         }
         .map(_ => 1)
         .exists
   }

   def checkUserExistence(userId: Int) = {
      _checkUserExistenceById(userId).result
   }

   private val _checkUserExistenceById = Compiled { userId: Rep[Int] =>
      userTable
         .filter { user =>
            user.id === userId
         }
         .map(_ => 1)
         .exists
   }

   def updateUser(user: User) = {
      userTable.insertOrUpdate(user)
   }

   //used when user updates itself
   def clientUpdateUser(user: User) = {
      _clientUpdateUser(user.id).update(user)
   }

   private val _clientUpdateUser = Compiled { userId: Rep[Int] =>
      userTable.filter { userTable =>
         userTable.state === UserState.ACTIVE && userTable.id === userId
      }
   }

   def getFriends(userId: Int) = {
      _getUserFriends(userId).result
   }

   private val _getUserFriends = Compiled { userId: Rep[Int] =>
      (userTable joinLeft imageTable on (_.imageId === _.id))
         .filter {
            case (user, _) =>
               user.state === UserState.ACTIVE && (user.id in {
                  userToUserRelationTable
                     .filter { userToUser =>
                        userToUser.userFrom === userId && userToUser.relation === UsersRelationType.FRIEND
                     }
                     .map(userToUser => userToUser.userTo)
               })
         }
         .sortBy { case (user, _) => user.id.desc }
   }

   def getFriendsIds(userId: Int) = {
      _getFriendsIds(userId).result
   }

   private val _getFriendsIds = Compiled { userId: Rep[Int] =>
      userToUserRelationTable
         .filter { userToUser =>
            userToUser.userFrom === userId && userToUser.relation === UsersRelationType.FRIEND
         }
         .map(userToUser => userToUser.userFrom)
   }

   def getFollowersIds(userId: Int) = {
      _getFollowersIds(userId).result
   }

   private val _getFollowersIds = Compiled { userId: Rep[Int] =>
      userToUserRelationTable
         .filter { userToUser =>
            userToUser.userTo === userId && userToUser.relation === UsersRelationType.FOLLOW
         }
         .map(userToUser => userToUser.userFrom)
   }

   def findUser(userId: Int, searchRegex: String) = {
      _findUser(userId, searchRegex).result
   }

   private val _findUser = Compiled {
      (userId: Rep[Int], searchRegex: Rep[String]) =>
         (userTable joinLeft imageTable on (_.imageId === _.id)).filter {
            case (user, _) =>
               ((user.username regexLike searchRegex) ||
                  (user.name regexLike searchRegex) ||
                  (user.surname regexLike searchRegex)) &&
                  (user.id =!= userId) &&
                  user.state === UserState.ACTIVE
         }
   }

   def getById(userId: Int) = {
      _getById(userId).result.head
   }

   private val _getById = Compiled { userId: Rep[Int] =>
      (userTable joinLeft imageTable on (_.imageId === _.id)).filter {
         case (user, _) =>
            user.id === userId && user.state === UserState.ACTIVE
      }
   }

   def createUserRelation(userToUser: UserToUserRelation) = {
      userToUserRelationTable.insertOrUpdate(userToUser)
   }

   def checkIsBlocked(userBlocked: Int, userBy: Int) = {
      _checkIsBlocked(userBlocked, userBy).result
   }

   private val _checkIsBlocked = Compiled {
      (userBlocked: Rep[Int], userBy: Rep[Int]) =>
         userToUserRelationTable
            .filter { usersRelation =>
               usersRelation.userTo === userBlocked &&
                  usersRelation.userFrom === userBy &&
                  usersRelation.relation === UsersRelationType.BLOCKED
            }
            .map(_ => 1)
            .exists
   }

   def removeUsersRelation(userToUser: UserToUserRelation) = {
      _findUsersRelation(userToUser.userFrom, userToUser.userTo).delete
   }

   private val _findUsersRelation = Compiled {
      (userFrom: Rep[Int], userTo: Rep[Int]) =>
         userToUserRelationTable
            .filter { userToUserTable =>
               (userToUserTable.userFrom === userFrom) && (userToUserTable.userTo === userTo)
            }
   }

   def getByUsername(username: String) = {
      _getByUserName(username).result.headOption
   }

   private val _getByUserName = Compiled { username: Rep[String] =>
      userTable
         .filter { user =>
            user.username === username
         }
   }

   def getTokenByUsername(username: String) = {
      _getTokenByUsername(username).result.headOption
   }

   private val _getTokenByUsername = Compiled { username: Rep[String] =>
      userTable
         .filter { user =>
            user.username === username
         }
         .map(user => user.token)
   }

}
