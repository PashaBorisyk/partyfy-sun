package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Image, User}
import services.database.UserServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserServiceImpl])
trait UserService[T[_]] {

   def getUsersByEventId(eventId: Long)(implicit token: TokenRepPrivate): T[Seq[(User, Option[Image])]]

   def checkUserExistence(nickname: String): T[Boolean]

   def updateUser(user: User)(implicit token: TokenRepPrivate): T[User]

   def getFriends(userId: Long)(implicit token: TokenRepPrivate): T[Seq[(User, Serializable)]]

   def getFriendsIds(userId: Long)(implicit token: TokenRepPrivate): T[Seq[Long]]

   def findUser(query: String)(implicit token: TokenRepPrivate):T[Seq[(User, Option[Image])]]

   def getById(id: Long)(implicit token: TokenRepPrivate): T[(User, Option[Image])]

   def addUserToFriends(userId: Long)(implicit token: TokenRepPrivate): T[Int]

   def removeUserFromFriends(userId: Long)(implicit token: TokenRepPrivate): T[Int]

   def login(username: String, password: String): T[Option[String]]

}
