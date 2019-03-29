package dao.traits

import com.google.inject.ImplementedBy
import dao.EventDAOImpl
import models.persistient.{Event, Image}

import scala.language.higherKinds

@ImplementedBy(classOf[EventDAOImpl])
trait EventDAO[T[_]] {

   def getEventById(id: Long): T[Seq[(Event, Option[Image])]]

   def deleteById(id: Long): T[Int]

   def create(event: Event, usersIds: Array[Int]): T[Long]

   def update(event: Event): T[Int]

   def getEventsByOwner(userId: Int): T[Seq[(Event, Option[Image])]]

   def getEventsByMemberId(userId: Int): T[Seq[(Event, Option[Image])]]

   def getEventIdsByMemberId(userId: Int): T[Seq[Long]]

   def getEvents(userId: Int,
                 latitude: Double,
                 longtitude: Double,
                 lastReadEventId: Long): T[Seq[(Event, Option[Image])]]

   def addUserToEvent(eventId: Long, userId: Int): T[Int]

   def cancelEvent(eventId: Long, userId: Int): T[Int]

   def removeUserFromEvent(eventId: Long, userId: Int): T[Int]

   def test()
}
