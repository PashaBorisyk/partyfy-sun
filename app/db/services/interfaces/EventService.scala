package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.EventServiceImpl
import models.Event

import scala.concurrent.Future

@ImplementedBy(classOf[EventServiceImpl])
trait EventService {

  def getEventById(id:Long):Future[Seq[(Event,Serializable with Product)]]
  def delete(id:Long):Future[Int]
  def create(event: (Event,Set[Long])) : Future[(Event,Set[Long])]
  def update(event: Event):Future[Event]
  def getEventsByOwner(userId:Long):Future[Seq[(Event,Serializable with Product)]]
  def getEventsByMemberId(userId:Long):Future[Seq[(Event,Serializable with Product)]]
  def getEventIdsByMemberId(userId:Long):Future[Seq[Long]]
  def getEvents(userId:Long,latitude:Double,longtitude:Double,lastReadEventId:Long):Future[Seq[(Event,Serializable with Product)]]
  def addMemberToEvent(userId:Long,eventId:Long, advancedUserId:Long):Future[Any]
  def cancelEvent(userId:Long,eventId:Long):Future[Any]
  def removeMember(userId:Long,advancedUserId:Long,eventId:Long):Future[Any]

}
