package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Event, Image}
import services.database.EventServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[EventServiceImpl])
trait EventService[T[_]] {

   def getEventById(id: Long)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def delete(id: Long)(implicit token: TokenRepPrivate): T[Int]

   def create(event: (Event, Array[Int]))(
      implicit token: TokenRepPrivate): T[Long]

   def update(event: Event)(implicit token: TokenRepPrivate): T[Int]

   def getEventsByOwner(userId: Int)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def getEventsByMemberId(userId: Int)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def getEventIdsByMemberId(userId: Int)(
      implicit token: TokenRepPrivate): T[Seq[Long]]

   def getEvents(latitude: Double, longtitude: Double, lastReadEventId: Long)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def addUserToEvent(eventId: Long, userId: Int)(
      implicit token: TokenRepPrivate): T[Int]

   def cancelEvent(eventId: Long)(implicit token: TokenRepPrivate): T[Int]

   def removeUserFromEvent(eventId: Long, userId: Int)(
      implicit token: TokenRepPrivate): T[Int]

   def test()
}
