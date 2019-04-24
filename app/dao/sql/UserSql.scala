package dao.sql

import dao.sql.implicits._
import dao.sql.tables.implicits._
import dao.sql.tables.{EventToUserTable, ImageTable, UserTable, UserToUserRelationTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

private[dao] object UserSql {

   private val eventToUserTable = TableQuery[EventToUserTable]
   private val userTable = TableQuery[UserTable]
   private val userToUserRelationTable = TableQuery[UserToUserRelationTable]
   private val imageTable = TableQuery[ImageTable]

   def getUsersByeventIDJoinImage(eventID: Long) = {
      _getUsersByeventIDJoinImage(eventID).result
   }

   private val _getUsersByeventIDJoinImage = Compiled { eventID: Rep[Long] =>
      (userTable joinLeft imageTable on (_.imageID === _.id)).filter {
         case (user, _) =>
            (user.state === UserState.ACTIVE) && (user.id in eventToUserTable
               .filter { eventToUser =>
                  eventToUser.eventID === eventID
               }
               .map(_.userID))
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

   def checkUserExistence(userID: Int) = {
      _checkUserExistenceById(userID).result
   }

   private val _checkUserExistenceById = Compiled { userID: Rep[Int] =>
      userTable
         .filter { user =>
            user.id === userID
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

   private val _clientUpdateUser = Compiled { userID: Rep[Int] =>
      userTable.filter { userTable =>
         userTable.state === UserState.ACTIVE && userTable.id === userID
      }
   }

   def getFriends(userID: Int) = {
      _getUserFriends(userID).result
   }

   private val _getUserFriends = Compiled { userID: Rep[Int] =>
      (userTable joinLeft imageTable on (_.imageID === _.id))
         .filter {
            case (user, _) =>
               user.state === UserState.ACTIVE && (user.id in {
                  userToUserRelationTable
                     .filter { userToUser =>
                        userToUser.userFrom === userID && userToUser.relation === UsersRelationType.FRIEND
                     }
                     .map(userToUser => userToUser.userTo)
               })
         }
         .sortBy { case (user, _) => user.id.desc }
   }

   def getFriendsIds(userID: Int) = {
      _getFriendsIds(userID).result
   }

   private val _getFriendsIds = Compiled { userID: Rep[Int] =>
      userToUserRelationTable
         .filter { userToUser =>
            userToUser.userFrom === userID && userToUser.relation === UsersRelationType.FRIEND
         }
         .map(userToUser => userToUser.userFrom)
   }

   def getFollowersIds(userID: Int) = {
      _getFollowersIds(userID).result
   }

   private val _getFollowersIds = Compiled { userID: Rep[Int] =>
      userToUserRelationTable
         .filter { userToUser =>
            userToUser.userTo === userID && userToUser.relation === UsersRelationType.FOLLOW
         }
         .map(userToUser => userToUser.userFrom)
   }

   def findUser(userID: Int, searchRegex: String) = {
      _findUser(userID, searchRegex).result
   }

   private val _findUser = Compiled {
      (userID: Rep[Int], searchRegex: Rep[String]) =>
         (userTable joinLeft imageTable on (_.imageID === _.id)).filter {
            case (user, _) =>
               ((user.username regexLike searchRegex) ||
                  (user.name regexLike searchRegex) ||
                  (user.surname regexLike searchRegex)) &&
                  (user.id =!= userID) &&
                  user.state === UserState.ACTIVE
         }
   }

   def getById(userID: Int) = {
      _getById(userID).result.head
   }

   private val _getById = Compiled { userID: Rep[Int] =>
      (userTable joinLeft imageTable on (_.imageID === _.id)).filter {
         case (user, _) =>
            user.id === userID && user.state === UserState.ACTIVE
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

   def getusersIDssByeventID(eventID: Long) = {
      _getusersIDssByeventID(eventID).result
   }

   private val _getusersIDssByeventID = Compiled { eventID: Rep[Long] =>
      eventToUserTable.filter { eventToUser => eventToUser.eventID === eventID }.map(_.userID)
   }

   def searchUser(userID: Int, searchRegex: String) = {
      _searchUser(userID, searchRegex).result
   }

   private val _searchUser = Compiled {
      (userID: Rep[Int], searchRegex: Rep[String]) =>
         (userTable join imageTable on (_.imageID === _.id)).filter {
            case (user, _) =>
               ((user.username regexLike searchRegex) ||
                  (user.name regexLike searchRegex) ||
                  (user.surname regexLike searchRegex)) &&
                  ((user.id =!= userID) &&
                  user.state === UserState.ACTIVE)
         }.map {
            case (user, image) => (user.id, user.username, image.urlSmall)
         }
   }

}
