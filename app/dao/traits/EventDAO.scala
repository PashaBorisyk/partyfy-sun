package dao.traits

import com.google.inject.ImplementedBy
import dao.EventDAOImpl
import models.persistient.{Event, Image}

import scala.language.higherKinds

@ImplementedBy(classOf[EventDAOImpl])
trait EventDAO[T[_]] {

   def getEventById(id: Long): T[Seq[(Event, Option[Image])]]

   def deleteById(id: Long) : T[Int]

   def create(event: (Event, Set[Long])) : T[Long]

   def update(event: Event) : T[Int]

   def getEventsByOwner(userId: Long): T[Seq[(Event, Option[Image])]]

   def getEventsByMemberId(userId: Long): T[Seq[(Event, Option[Image])]]

   def getEventIdsByMemberId(userId: Long): T[Seq[Long]]

   def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long): T[Seq[(Event, Option[Image])]]

   def addMemberToEvent(userId: Long, eventId: Long, advancedUserId: Long): T[Int]

   def cancelEvent(userId: Long, eventId: Long): T[Long]

   def removeMember(userId: Long, advancedUserId: Long, eventId: Long): T[Int]

   def test()
}
