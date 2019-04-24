package dao.traits

import com.google.inject.ImplementedBy
import dao.UserDAOImpl
import models.dto.SearchableUserForm
import models.persistient.{Image, User, UserToUserRelation}

import scala.language.higherKinds

@ImplementedBy(classOf[UserDAOImpl])
trait UserDAO[T[_]] {

   def getUsersByeventID(eventID: Long): T[Seq[(User, Option[Image])]]

   def checkUserExistence(username: String): T[Boolean]

   def updateUser(user: User): T[User]

   def clientUpdateUser(user: User): T[User]

   def getFriends(userID: Int): T[Seq[(User, Option[Image])]]

   def getFriendsIds(userID: Int): T[Seq[Int]]

   def findUser(userID: Int, query: String): T[Seq[(User, Option[Image])]]

   def getById(userID: Int): T[(User, Option[Image])]

   def createUsersRelation(userToUser: UserToUserRelation): T[Int]

   def removeUsersRelation(userToUser: UserToUserRelation): T[Int]

   def getByUsername(username: String): T[Option[User]]

   def getTokenByuserID(username: String): T[Option[String]]

   def getusersIDssByeventID(eventID:Long) : T[Seq[Int]]

   def searchUser(userID:Int, query: String) : T[Seq[SearchableUserForm]]

}
