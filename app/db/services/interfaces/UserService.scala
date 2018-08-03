package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.UserServiceImpl
import models.{HipeImage, User}
import play.api.mvc.Request

import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def getUsersByEventId(eventId:Long)(implicit request: Request[_]) : Future[Seq[(User,Serializable with Product)]]
  def checkUserExistence(nickname:String)(implicit request: Request[_]):Future[Boolean]
  def registerUser(user:User)(implicit request: Request[_]): Future[Any]
  def updateUser(user:User)(implicit request: Request[_]):Future[Int]
  def getFriends(usserId:Long)(implicit request: Request[_]):Future[Seq[(User,Serializable with Product)]]
  def getFriendsIds(userId:Long)(implicit request: Request[_]):Future[Seq[Long]]
  def findUser(userId:Long,query:String)(implicit request: Request[_]):Future[Seq[(User,Serializable with Product)]]
  def getById(id:Long)(implicit request: Request[_]):Future[(User, HipeImage)]
  def addUserToFriends(userId:Long,advancedUsserId:Long)(implicit request: Request[_]):Future[Int]
  def removeUserFromFriends(userId:Long,advancedUserId:Long)(implicit request: Request[_]):Future[Int]

}
