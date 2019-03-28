package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Image, User, UsersRelationType}
import services.database.UserServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[UserServiceImpl])
trait UserService[T[_]] {

   def getUsersByEventId(eventId: Long)(implicit token: TokenRepPrivate): T[Seq[(User, Option[Image])]]

   def checkUserExistence(nickname: String): T[Boolean]

   def clientUpdateUser(user: User)(implicit token: TokenRepPrivate): T[User]

   def getFriends(userId: Int)(implicit token: TokenRepPrivate): T[Seq[(User, Serializable)]]

   def getFriendsIds(userId: Int)(implicit token: TokenRepPrivate): T[Seq[Int]]

   def findUser(query: String)(implicit token: TokenRepPrivate):T[Seq[(User, Option[Image])]]

   def getById(userId: Int)(implicit token: TokenRepPrivate): T[(User, Option[Image])]

   def createUsersRelation(userId: Int,relationType:String)(implicit token: TokenRepPrivate): T[Int]

   def removeUsersRelation(userId: Int)(implicit token: TokenRepPrivate): T[Int]

   def login(username: String, password: String): T[Option[String]]

}
