package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Event, Image}
import services.database.EventServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[EventServiceImpl])
trait EventService[T[_]] {

   def getEventById(id: Long)(
      implicit token: TokenRepPrivate): T[Option[(Event, Option[Image])]]

   def delete(id: Long)(implicit token: TokenRepPrivate): T[Int]

   def create(event: (Event, Array[Int]))(
      implicit token: TokenRepPrivate): T[Long]

   def update(event: Event)(implicit token: TokenRepPrivate): T[Int]

   def getEventsByOwner(userID: Int)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def getEventsByMemberId(userID: Int)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def getEventIDsByMemberId(userID: Int)(
      implicit token: TokenRepPrivate): T[Seq[Long]]

   def getEvents(latitude: Double, longtitude: Double, lastReadeventID: Long)(
      implicit token: TokenRepPrivate): T[Seq[(Event, Option[Image])]]

   def addUserToEvent(eventID: Long, userID: Int)(
      implicit token: TokenRepPrivate): T[Int]

   def cancelEvent(eventID: Long)(implicit token: TokenRepPrivate): T[Int]

   def removeUserFromEvent(eventID: Long, userID: Int)(
      implicit token: TokenRepPrivate): T[Int]


   def test()
}
