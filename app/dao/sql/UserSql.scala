package dao.sql

import dao.sql.implicits._
import dao.sql.tables.implicits._
import dao.sql.tables.{EventToUserTable, ImageTable, UserTable, UserToUserRelationTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

private[dao] object UserSql {

   private val eventToUserTable = TableQuery[EventToUserTable]
   private val userTable = TableQuery[UserTable]
   private val userToUserRelationTable = TableQuery[UserToUserRelationTable]
   private val imageTable = TableQuery[ImageTable]

   def getUsersByEventId(eventId: Long) = {

      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { case (user, _) =>
         (user.state === UserState.ACTIVE) && (user.id in eventToUserTable.filter { eventToUser =>
            eventToUser.eventId === eventId
         }.map(_.userId))
      }.result
   }

   def create(user: User) = {
      (userTable returning userTable.map(_.id) into ((user, id) => user.copy(id))) += user
   }

   def checkUserExistence(username: String) = {
      userTable.filter { user =>
         user.username === username
      }.map(_ => 1).exists.result
   }

   def checkUserExistence(userId: Int) = {
      userTable.filter { user =>
         user.id === userId
      }.map(_ => 1).exists.result
   }

   def updateUser(user: User)(implicit ec: ExecutionContext) = {
      userTable.insertOrUpdate(user).map(_ => user)
   }

   //used when user updates itself
   def clientUpdateUser(user: User)(implicit ec: ExecutionContext) = {
      userTable.filter {
         userTable =>
            userTable.state === UserState.ACTIVE && userTable.id === user.id
      }.update(user).map(_ => user)
   }

   def getFriends(userId: Int) = {
      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { case (user, _) =>
         user.state === UserState.ACTIVE && (user.id in {
            userToUserRelationTable.filter { userToUser =>
               userToUser.userFrom === userId && userToUser.relation === UsersRelationType.FRIEND
            }.map(userToUser => userToUser.userTo)
         })
      }.sortBy { case (user, _) => user.id.desc }.result
   }

   def getFriendsIds(userId: Int) = {
      userToUserRelationTable.filter { userToUser =>
         userToUser.userFrom === userId && userToUser.relation === UsersRelationType.FRIEND
      }.map(userToUser => userToUser.userFrom).result
   }

   def getFollowersIds(userId: Int) = {
      userToUserRelationTable.filter { userToUser =>
         userToUser.userTo === userId && userToUser.relation === UsersRelationType.FOLLOW
      }.map(userToUser => userToUser.userFrom).result
   }

   def findUser(userId: Int, searchRegex: String) = {

      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { case (user, _) =>
         ((user.username regexLike searchRegex) ||
            (user.name regexLike searchRegex) ||
            (user.surname regexLike searchRegex))&&
            (user.id =!= userId) &&
            user.state === UserState.ACTIVE
      }.result

   }

   def getById(userId: Int) = {
      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { case (user, _) =>
         user.id === userId && user.state === UserState.ACTIVE
      }.result.head
   }

   def createUserRelation(userToUser: UserToUserRelation) = {
      userToUserRelationTable.insertOrUpdate(userToUser)
   }

   def checkIsBlocked(userBlocked: Int, userBy: Int) = {
      userToUserRelationTable.filter { usersRelation =>
         usersRelation.userTo === userBlocked &&
            usersRelation.userFrom === userBy &&
            usersRelation.relation === UsersRelationType.BLOCKED
      }.map(_ => 1).exists.result
   }

   def removeUsersRelation(userToUser: UserToUserRelation) = {
      userToUserRelationTable
         .filter {
            userToUserTable =>
               (userToUserTable.userFrom === userToUser.userFrom) && (userToUserTable.userTo === userToUser.userTo)
         }.delete
   }

   def getByUsername(username: String) = {
      userTable
         .filter { user => user.username === username }
         .result
         .headOption
   }

   def getTokenByUsername(username: String) = {
      userTable.filterIf(username.nonEmpty) { user => user.username === username }
         .map(user => user.token)
         .result
         .headOption
   }

}
