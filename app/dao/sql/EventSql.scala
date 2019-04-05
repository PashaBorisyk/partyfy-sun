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
      (eventTable joinLeft imageTable on (_.eventimageID === _.id))
         .filter { case (event, image) => event.id === id }
   }

   def getById(eventID: Long) = {
      _getById(eventID).result.headOption
   }

   private val _getById = Compiled { eventID: Rep[Long] =>
      eventTable.filter(_.id === eventID)
   }

   def getByOwnerIdJoinImage(userID: Int) = {
      _getByOwnerIdJoinImage(userID).result
   }

   private val _getByOwnerIdJoinImage = Compiled { userID: Rep[Int] =>
      (eventTable joinLeft imageTable on (_.eventimageID === _.id))
         .filter {
            case (event, _) =>
               event.ownerId ===
                  userID
         }
         .sortBy(_._1.dateMills.desc)
   }

   def getByMemberIdJoinImage(userID: Int)(implicit ec: ExecutionContext) = {
      _getByMemberId(userID).result
   }

   private val _getByMemberId = Compiled { userID: Rep[Int] =>
      (eventTable joinLeft imageTable on (_.eventimageID === _.id)).filter {
         event =>
            event._1.id in eventToUserTable
               .filter { event =>
                  event.userID === userID
               }
               .map(_.eventID)
      }
   }

   def getIdsByMemberId(userID: Int) = {
      _getIdsByMemberId(userID).result
   }

   private val _getIdsByMemberId = Compiled { userID: Rep[Int] =>
      eventTable
         .filter { event =>
            event.id in eventToUserTable
               .filter { eventToUser =>
                  eventToUser.userID === userID
               }
               .map(_.eventID)
         }
         .map(_.id)
   }

   def getPublicEvents(userID: Int,
                       latitude: Double,
                       longtitude: Double,
                       lastReadeventID: Long) = {

      _getPublicEvents(latitude, longtitude, lastReadeventID).result
   }

   private val _getPublicEvents = Compiled {
      (latitude: Rep[Double],
       longtitude: Rep[Double],
       lastReadeventID: Rep[Long]) =>
         (eventTable joinLeft imageTable on (_.eventimageID === _.id))
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
            .filter(_._1.id > lastReadeventID)
            .take(30)
            .sortBy(_._1.creationDateMills.desc)
   }

   def addUserToEvent(eventID: Long, userID: Int) = {
      eventToUserTable += EventToUser(eventID, userID)
   }

   def deleteUserFromEvent(userID: Int, eventID: Long) = {
      _deleteUserFromEvent(userID, eventID).delete
   }

   private val _deleteUserFromEvent = Compiled {
      (userID: Rep[Int], eventID: Rep[Long]) =>
         eventToUserTable.filter { eventToUser =>
            eventToUser.userID === userID && eventToUser.eventID === eventID
         }
   }

   def cancelEvent(eventID: Long, ownerId: Int) = {
      _cancelEvent(eventID, ownerId).update(EventState.CANCELED)
   }

   private val _cancelEvent = Compiled {
      (eventID: Rep[Long], ownerId: Rep[Int]) =>
         eventTable
            .filter { event =>
               event.id === eventID &&
                  event.ownerId === ownerId
            }
            .map(_.state)
   }

}
