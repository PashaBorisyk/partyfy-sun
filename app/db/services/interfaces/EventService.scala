package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.EventServiceImpl
import models.Event
import play.api.mvc.Request

import scala.language.higherKinds

@ImplementedBy(classOf[EventServiceImpl])
trait EventService[T[_]] {

   def getEventById(id: Long)(implicit request: Request[_]): T[Seq[(Event, Serializable with Product)]]

   def delete(id: Long)(implicit request: Request[_]): T[Int]

   def create(event: (Event, Set[Long]))(implicit request: Request[_]): T[(Event, Set[Long])]

   def update(event: Event)(implicit request: Request[_]): T[Int]

   def getEventsByOwner(userId: Long)(implicit request: Request[_]): T[Seq[(Event, Serializable with Product)]]

   def getEventsByMemberId(userId: Long)(implicit request: Request[_]): T[Seq[(Event, Serializable with Product)]]

   def getEventIdsByMemberIdForSocket(userId: Long)(implicit request: Request[_]): T[Seq[Long]]

   def getEventIdsByMemberId(userId: Long)(implicit request: Request[_]): T[Seq[Long]]

   def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long)(implicit request: Request[_]): T[Seq[(Event, Serializable with Product)]]

   def addMemberToEvent(userId: Long, eventId: Long, advancedUserId: Long)(implicit request: Request[_]): T[Int]

   def cancelEvent(userId: Long, eventId: Long)(implicit request: Request[_]): T[Any]

   def removeMember(userId: Long, advancedUserId: Long, eventId: Long)(implicit request: Request[_]): T[Any]

   def test()
}
