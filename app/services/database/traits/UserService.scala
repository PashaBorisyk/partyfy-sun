package services.database.traits

import com.google.inject.ImplementedBy
import models.User
import services.database.UserServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserServiceImpl])
trait UserService[T[_]] {

   def getUsersByEventId(eventId: Long, token: String): T[Seq[(User, Serializable with Product)]]

   def checkUserExistence(nickname: String): T[Boolean]

   def updateUser(user: User, token: String): T[String]

   def getFriends(userId: Long, token: String): T[Seq[(User, Serializable with Product)]]

   def getFriendsIds(userId: Long, token: String): T[Seq[Long]]

   def findUser(userId: Long, query: String, token: String): T[Seq[(User, Serializable with Product)]]

   def getById(id: Long, token: String): T[(User, Serializable with Product)]

   def addUserToFriends(userId: Long, token:String): T[Int]

   def removeUserFromFriends(userId: Long, token:String): T[Int]

   def login(username: String, password: String): T[Option[String]]

}
