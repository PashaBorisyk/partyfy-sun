package dao.traits

import com.google.inject.ImplementedBy
import dao.UserDAOImpl
import models.persistient.{Image, User}

import scala.language.higherKinds

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO[T[_]] {

   def getUsersByEventId(eventId: Long): T[Seq[(User, Option[Image])]]

   def checkUserExistence(username: String) : T[Boolean]

   def updateUser(user: User): T[User]

   def getFriends(userId: Long): T[Seq[(User, Option[Image])]]

   def getFriendsIds(userId: Long): T[Seq[Long]]

   def findUser(userId: Long, query: String): T[Seq[(User, Option[Image])]]

   def getById(id: Long): T[(User, Option[Image])]

   def addUserToFriends(userId: Long, addedUserId: Long) : T[Int]

   def removeUserFromFriends(userId: Long, removedUserId: Long) : T[Int]

   def getByUsername(username: String): T[Option[User]]

   def getTokenByUserId(username:String) : T[Option[String]]

}
