package dao.sql

import dao.sql.tables.implicits._
import dao.sql.tables.{EventTable, EventToUserTable, ImageTable}
import javax.inject.Singleton
import models.persistient.{EventToUser, _}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

@Singleton
private[dao] object EventSql {

   private val eventTable = TableQuery[EventTable]
   private val eventToUserTable = TableQuery[EventToUserTable]
   private val imageTable = TableQuery[ImageTable]

   def deleteById(id: Long) = {
      eventTable.filter(_.id === id).delete
   }

   def insertEvent(event: Event) = {
      eventTable returning eventTable.map(event => event.id) += event
   }

   def addUsersToEvent(eventToUsers: Array[EventToUser]) = {
      eventToUserTable ++= eventToUsers
   }

   def update(event: Event) = {
      eventTable.insertOrUpdate(event)
   }

   def getByIdJoinImages(id: Long) = {
      _getByIdJoinImages(id).result
   }

   private val _getByIdJoinImages = Compiled { id: Rep[Long] =>
      (eventTable joinLeft imageTable on (_.eventImageId === _.id))
         .filter { case (event, image) => event.id === id }
   }

   def getById(eventId: Long) = {
      _getById(eventId).result.headOption
   }

   private val _getById = Compiled { eventId: Rep[Long] =>
      eventTable.filter(_.id === eventId)
   }

   def getByOwnerIdJoinImage(userId: Int) = {
      _getByOwnerIdJoinImage(userId).result
   }

   private val _getByOwnerIdJoinImage = Compiled { userId: Rep[Int] =>
      (eventTable joinLeft imageTable on (_.eventImageId === _.id))
         .filter {
            case (event, _) =>
               event.ownerId ===
                  userId
         }
         .sortBy(_._1.dateMills.desc)
   }

   def getByMemberIdJoinImage(userId: Int)(implicit ec: ExecutionContext) = {
      _getByMemberId(userId).result
   }

   private val _getByMemberId = Compiled { userId: Rep[Int] =>
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter {
         event =>
            event._1.id in eventToUserTable
               .filter { event =>
                  event.userId === userId
               }
               .map(_.eventId)
      }
   }

   def getIdsByMemberId(userId: Int) = {
      _getIdsByMemberId(userId).result
   }

   private val _getIdsByMemberId = Compiled { userId: Rep[Int] =>
      eventTable
         .filter { event =>
            event.id in eventToUserTable
               .filter { eventToUser =>
                  eventToUser.userId === userId
               }
               .map(_.eventId)
         }
         .map(_.id)
   }

   def getPublicEvents(userId: Int,
                       latitude: Double,
                       longtitude: Double,
                       lastReadEventId: Long) = {

      _getPublicEvents(latitude, longtitude, lastReadEventId).result
   }

   private val _getPublicEvents = Compiled {
      (latitude: Rep[Double],
       longtitude: Rep[Double],
       lastReadEventId: Rep[Long]) =>
         (eventTable joinLeft imageTable on (_.eventImageId === _.id))
            .filter {
               case (event, _) =>
                  event.privacyType === EventPrivacyType.PUBLIC &&
                     event.latitude > (latitude - 0.3) &&
                     event.latitude < (latitude + 0.3)
            }
            .filter {
               case (event, _) =>
                  event.longtitude > (longtitude - 0.3) && event.longtitude < (longtitude + 0.3)
            }
            .filter(_._1.id > lastReadEventId)
            .take(30)
            .sortBy(_._1.creationDateMills.desc)
   }

   def addUserToEvent(eventId: Long, userId: Int) = {
      eventToUserTable += EventToUser(eventId, userId)
   }

   def deleteUserFromEvent(userId: Int, eventId: Long) = {
      _deleteUserFromEvent(userId, eventId).delete
   }

   private val _deleteUserFromEvent = Compiled {
      (userId: Rep[Int], eventId: Rep[Long]) =>
         eventToUserTable.filter { eventToUser =>
            eventToUser.userId === userId && eventToUser.eventId === eventId
         }
   }

   def cancelEvent(eventId: Long, ownerId: Int) = {
      _cancelEvent(eventId, ownerId).update(EventState.CANCELED)
   }

   private val _cancelEvent = Compiled {
      (eventId: Rep[Long], ownerId: Rep[Int]) =>
         eventTable
            .filter { event =>
               event.id === eventId &&
                  event.ownerId === ownerId
            }
            .map(_.state)
   }

}
