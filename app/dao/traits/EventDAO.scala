package dao.traits

import com.google.inject.ImplementedBy
import dao.EventDAOImpl
import models.persistient.{Event, Image}

import scala.language.higherKinds

@ImplementedBy(classOf[EventDAOImpl])
trait EventDAO[T[_]] {

   def getEventById(id: Long): T[Seq[(Event, Option[Image])]]

   def deleteById(id: Long): T[Int]

   def create(event: Event, usersIDss: Array[Int]): T[Long]

   def update(event: Event): T[Int]

   def getEventsByOwner(userID: Int): T[Seq[(Event, Option[Image])]]

   def getEventsByMemberId(userID: Int): T[Seq[(Event, Option[Image])]]

   def getEventIDsByMemberID(userID: Int): T[Seq[Long]]

   def getEvents(userID: Int,
                 latitude: Double,
                 longtitude: Double,
                 lastReadeventID: Long): T[Seq[(Event, Option[Image])]]

   def addUserToEvent(eventID: Long, userID: Int): T[Int]

   def cancelEvent(eventID: Long, userID: Int): T[Int]

   def removeUserFromEvent(eventID: Long, userID: Int): T[Int]

   def test()
}
