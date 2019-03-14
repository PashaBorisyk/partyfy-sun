package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Event, Image}
import services.database.EventServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[EventServiceImpl])
trait EventService[T[_]] {

   def getEventById(id: Long)(implicit token:TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def delete(id: Long)(implicit token:TokenRepPrivate): T[Int]

   def create(event: (Event, Set[Long]))(implicit token:TokenRepPrivate): T[Long]

   def update(event: Event)(implicit token:TokenRepPrivate): T[Int]

   def getEventsByOwner(userId: Long)(implicit token:TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def getEventsByMemberId(userId: Long)(implicit token:TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def getEventIdsByMemberId(userId: Long)(implicit token:TokenRepPrivate): T[Seq[Long]]

   def getEvents(latitude: Double, longtitude: Double, lastReadEventId: Long)(implicit token:TokenRepPrivate): T[Seq[
      (Event, Option[Image])]]

   def addMemberToEvent(userId: Long, eventId: Long, advancedUserId: Long)(implicit token:TokenRepPrivate): T[Int]

   def cancelEvent(userId: Long, eventId: Long)(implicit token:TokenRepPrivate): T[Long]

   def removeMember(userId: Long, advancedUserId: Long, eventId: Long)(implicit token:TokenRepPrivate): T[Int]

   def test()
}
