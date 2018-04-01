package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.UserServiceImpl
import models.{HipeImage, User, UserDAO}

import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def getUsersByEventId(eventId:Long) : Future[Seq[(User,UserDAO#TableElementType)]]
  def checkUserExistence(nickname:String):Future[Boolean]
  def registerUser(user:User): Future[Any]
  def updateUser(user:User):Future[Int]
  def getFriends(usserId:Long):Future[Seq[(User,UserDAO#TableElementType)]]
  def getFriendsIds(userId:Long):Future[Seq[Long]]
  def findUser(userId:Long,query:String):Future[Seq[(User,UserDAO#TableElementType)]]
  def getById(id:Long):Future[(User, HipeImage)]
  def addUserToFriends(userId:Long,advancedUsserId:Long):Future[Int]
  def removeUserFromFriends(userId:Long,advancedUserId:Long):Future[Int]

}
