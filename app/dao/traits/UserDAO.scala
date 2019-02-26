package dao.traits

import com.google.inject.ImplementedBy
import dao.UserDAOImpl
import models.persistient.User
import services.traits.TokenRepresentation

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO[T[_]] {

   def getUsersByEventId(eventId: Long, token: TokenRepresentation): T[Seq[(User, Serializable)]]

   def checkUserExistence(nickname: String): T[Boolean]

   def updateUser(user: User, token: TokenRepresentation): T[String]

   def getFriends(userId: Long, token: TokenRepresentation): T[Seq[(User, Serializable)]]

   def getFriendsIds(userId: Long, token: TokenRepresentation): T[Seq[Long]]

   def findUser(query: String, token: TokenRepresentation):T[Seq[(User, Serializable with Product)]]

   def getById(id: Long, token: TokenRepresentation): T[(User, Serializable)]

   def addUserToFriends(userId: Long, token: TokenRepresentation): T[Int]

   def removeUserFromFriends(userId: Long, token: TokenRepresentation): T[Int]

   def login(username: String, password: String): T[Option[String]]

}
