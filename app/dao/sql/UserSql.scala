package dao.sql

import dao.sql.tables.{EventToUserTable, ImageTable, UserTable, UserToUserTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._
import implicits._
import tables.implicits._

import scala.concurrent.ExecutionContext

private[dao] object UserSql {

   private val eventToUserTable = TableQuery[EventToUserTable]
   private val userTable = TableQuery[UserTable]
   private val userToUserTable = TableQuery[UserToUserTable]
   private val imageTable = TableQuery[ImageTable]

   def getUsersByEventId(eventId: Long) = {

      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { userWithImage =>
         userWithImage._1.id in eventToUserTable.filter { eventToUser =>
            eventToUser.eventId === eventId
         }.map(_.userId)
      }.result
   }

   def create(user:User) = {
      (userTable returning userTable.map(_.id) into((user,id) => user.copy(id))) += user
   }

   def checkUserExistence(username: String) = {
      userTable.filter { user =>
         user.username === username
      }.map(_.id).exists.result
   }

   def updateUser(user: User) (implicit ec:ExecutionContext) = {
      userTable.update(user).map(_ => user)
   }

   //used when user updates itself
   def clientUpdateUser(user: User) (implicit ec:ExecutionContext) ={
      userTable.filter{
         userTable =>
            userTable.state === UserState.ACTIVE && userTable.id === user.id
      }.update(user).map(_ => user)
   }

   def getFriends(userId: Long) = {
      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { userWithImage =>
         userWithImage._1.id in {
            userToUserTable.filter { userToUser =>
               userToUser.user_from === userId
            }.map(userToUser => userToUser.user_to)
         }
      }.sortBy(userWithImage => userWithImage._1.id.desc).result
   }

   def getFriendsIds(userId: Long) = {
      userToUserTable.filter { userToUser =>
         userToUser.user_from === userId
      }.map(userToUser => userToUser.user_to).result
   }

   def findUser(userId: Long, searchRegex: String) = {

      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { userWithImage =>
         ((userWithImage._1.username regexLike searchRegex) ||
            (userWithImage._1.name regexLike searchRegex) ||
            (userWithImage._1.surname regexLike searchRegex)) && (userWithImage._1.id =!= userId)
      }.result

   }

   def getById(userId: Long) = {
      (userTable joinLeft imageTable on (_.imageId === _.id)).filter { userWithImage =>
         userWithImage._1.id === userId
      }.result.head
   }

   def addUserToFriends(userId: Long, addedUserId: Long) = {
      userToUserTable += UserToUser(userId, addedUserId)
   }

   def removeUserFromFriends(userId: Long, removedUserId: Long) = {
      userToUserTable.filter { userToUser =>
         (userToUser.user_from === userId) && (userToUser.user_to === removedUserId)
      }.delete
   }

   def getByUsername(username: String) = {
      userTable.filter { user => user.username === username }.result.headOption
   }

   def getTokenByUsername(username:String) = {
      userTable.filterIf(username.nonEmpty){user => user.username === username}
         .map(user => user.token)
         .result
         .headOption
   }

}
