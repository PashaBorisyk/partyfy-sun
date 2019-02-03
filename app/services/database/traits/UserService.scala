package services.database.traits

import com.google.inject.ImplementedBy
import models.User
import play.api.mvc.Request
import services.database.UserServiceImpl

import scala.concurrent.Future
import scala.language.higherKinds

@ImplementedBy(classOf[UserServiceImpl])
trait UserService[T[_]] {

  def getUsersByEventId(eventId:Long)(implicit request: Request[_]) : Future[Seq[(User,Serializable with Product)]]
  def checkUserExistence(nickname:String)(implicit request: Request[_]):Future[Boolean]
  def updateUser(user:User)(implicit request: Request[_]):Future[Int]
  def getFriends(usserId:Long)(implicit request: Request[_]):Future[Seq[(User,Serializable with Product)]]
  def getFriendsIds(userId:Long)(implicit request: Request[_]):Future[Seq[Long]]
  def findUser(userId:Long,query:String)(implicit request: Request[_]):Future[Seq[(User,Serializable with Product)]]

  def getById(id: Long)(implicit request: Request[_]): Future[(User, Serializable with Product)]
  def addUserToFriends(userId:Long,advancedUsserId:Long)(implicit request: Request[_]):Future[Int]
  def removeUserFromFriends(userId:Long,advancedUserId:Long)(implicit request: Request[_]):Future[Int]

}
