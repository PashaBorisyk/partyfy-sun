package dao.traits

import com.google.inject.ImplementedBy
import dao.UserDAOImpl
import models.persistient.{Image, User, UserToUserRelation}

import scala.language.higherKinds

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO[T[_]] {

   def getUsersByEventId(eventId: Long): T[Seq[(User, Option[Image])]]

   def checkUserExistence(username: String) : T[Boolean]

   def updateUser(user: User): T[User]

   def clientUpdateUser(user:User) : T[User]

   def getFriends(userId: Long): T[Seq[(User, Option[Image])]]

   def getFriendsIds(userId: Long): T[Seq[Long]]

   def findUser(userId: Long, query: String): T[Seq[(User, Option[Image])]]

   def getById(id: Long): T[(User, Option[Image])]

   def createUsersRelation(userToUser: UserToUserRelation) : T[Int]

   def removeUsersRelation(userToUser: UserToUserRelation) : T[Int]

   def getByUsername(username: String): T[Option[User]]

   def getTokenByUserId(username:String) : T[Option[String]]

}
