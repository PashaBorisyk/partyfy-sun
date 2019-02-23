package services.database.traits

import com.google.inject.ImplementedBy
import models.persistient.Event
import play.api.mvc.Request
import services.database.EventServiceImpl
import services.traits.TokenRepresentation

import scala.language.higherKinds

@ImplementedBy(classOf[EventServiceImpl])
trait EventService[T[_]] {

   def getEventById(id: Long,token:TokenRepresentation): T[Array[(Event, Product with Serializable)]]

   def delete(id: Long,token:TokenRepresentation): T[Int]

   def create(event: (Event, Set[Long]),token:TokenRepresentation): T[Long]

   def update(event: Event,token:TokenRepresentation): T[Int]

   def getEventsByOwner(userId: Long,token:TokenRepresentation): T[Array[(Event, Product with Serializable)]]

   def getEventsByMemberId(userId: Long,token:TokenRepresentation): T[Array[(Event, Product with Serializable)]]

   def getEventIdsByMemberIdForSocket(userId: Long,token:TokenRepresentation): T[Array[Long]]

   def getEventIdsByMemberId(userId: Long,token:TokenRepresentation): T[Array[Long]]

   def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long,token:TokenRepresentation): T[Array[(Event, Product with Serializable)]]

   def addMemberToEvent(userId: Long, eventId: Long, advancedUserId: Long,token:TokenRepresentation): T[Int]

   def cancelEvent(userId: Long, eventId: Long,token:TokenRepresentation): T[Any]

   def removeMember(userId: Long, advancedUserId: Long, eventId: Long,token:TokenRepresentation): T[Any]

   def test()
}
