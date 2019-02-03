package services.database.traits.raw

import models.Event

import scala.language.higherKinds

trait EventInterface[T[_]] {

   def getEventById[R](id: Long): T[R]

   def delete[R](id: Long): T[R]

   def create[R](event: (Event, Set[Long])): T[R]

   def update[R](event: Event): T[R]

   def getEventsByOwner[R](userId: Long): T[R]

   def getEventsByMemberId[R](userId: Long): T[R]

   def getEventIdsByMemberId[R](userId: Long): T[R]

   def getEvents[R](userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long): T[R]

   def addMemberToEvent[R](userId: Long, eventId: Long, advancedUserId: Long): T[R]

   def cancelEvent[R](userId: Long, eventId: Long): T[R]

   def removeMember[R](userId: Long, advancedUserId: Long, eventId: Long): T[R]

}
