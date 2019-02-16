package services.database.traits

import com.google.inject.ImplementedBy
import models.User
import services.database.UserServiceImpl
import services.traits.TokenRep

import scala.language.higherKinds

@ImplementedBy(classOf[UserServiceImpl])
trait UserService[T[_]] {

   def getUsersByEventId(eventId: Long, token: TokenRep): T[Seq[(User, Serializable)]]

   def checkUserExistence(nickname: String): T[Boolean]

   def updateUser(user: User, token: TokenRep): T[String]

   def getFriends(userId: Long, token: TokenRep): T[Seq[(User, Serializable)]]

   def getFriendsIds(userId: Long, token: TokenRep): T[Seq[Long]]

   def findUser(query: String, token: TokenRep):T[Seq[(User, Serializable with Product)]]

   def getById(id: Long, token: TokenRep): T[(User, Serializable)]

   def addUserToFriends(userId: Long, token: TokenRep): T[Int]

   def removeUserFromFriends(userId: Long, token: TokenRep): T[Int]

   def login(username: String, password: String): T[Option[String]]

}
