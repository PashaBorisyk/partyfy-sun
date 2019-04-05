package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Image, User}
import services.database.UserServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserServiceImpl])
trait UserService[T[_]] {

   def getUsersByeventID(eventID: Long)(
      implicit token: TokenRepPrivate): T[Seq[(User, Option[Image])]]

   def checkUserExistence(nickname: String): T[Boolean]

   def clientUpdateUser(user: User)(implicit token: TokenRepPrivate): T[User]

   def getFriends(userID: Int)(
      implicit token: TokenRepPrivate): T[Seq[(User, Serializable)]]

   def getFriendsIds(userID: Int)(implicit token: TokenRepPrivate): T[Seq[Int]]

   def findUser(query: String)(
      implicit token: TokenRepPrivate): T[Seq[(User, Option[Image])]]

   def getById(userID: Int)(
      implicit token: TokenRepPrivate): T[(User, Option[Image])]

   def createUsersRelation(userID: Int, relationType: String)(
      implicit token: TokenRepPrivate): T[Int]

   def removeUsersRelation(userID: Int)(implicit token: TokenRepPrivate): T[Int]

   def login(username: String, password: String): T[Option[String]]

   def getusersIDssByeventID(eventID:Long)(implicit token:TokenRepPrivate) : T[Seq[Int]]

}
