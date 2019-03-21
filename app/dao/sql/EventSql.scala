package dao.sql

import dao.sql.tables.implicits._
import dao.sql.tables.{EventTable, EventToUserTable, ImageTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

private[dao] object EventSql {

   private val eventTable = TableQuery[EventTable]
   private val eventToUserTable = TableQuery[EventToUserTable]
   private val imageTable = TableQuery[ImageTable]

   def deleteById(id: Long) = {
      eventTable.filter(_.id === id).delete
   }

   def create(event: (Event, Set[Long]))(implicit ec: ExecutionContext) = {
      val insertQuery = eventTable returning eventTable.map(table => table.id)
      val execute = (insertQuery += event._1).flatMap { eventId =>

         val eventUserConnections = event._2.map { userId =>
            EventToUser(eventId, userId)
         }
         (eventToUserTable++=eventUserConnections).map(_=> eventId)
      }

      execute
   }

   def update(event: Event) = {
      eventTable.insertOrUpdate(event)
   }

   def getById(id: Long) = {
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter(_._1.id === id).result
   }

   def getByOwnerId(userId: Long) = {
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter(_._1.ownerId === userId).sortBy(_._1.id)
         .result
   }

   def getByMemberId(userId: Long)(implicit ec: ExecutionContext) = {
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter { event =>
         event._1.id in eventToUserTable.filter { event =>
            event.userId === userId
         }.map(_.eventId)
      }.result
   }

   def getIdsByMemberId(userId: Long) = {
      eventTable.filter { event =>
         event.id in eventToUserTable.filter { eventToUser => eventToUser.userId === userId }.map(_.eventId)
      }.map(_.id).result
   }

   def getPublicEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long) = {
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter {
         case (event, _) =>
            event.privacyType === EventPrivacyType.PUBLIC &&
               event.latitude > (latitude - 0.3) &&
               event.latitude < (latitude + 0.3)
      }.filter {
         case (event, _) =>
            event.longtitude > (longtitude - 0.3) && event.longtitude < (longtitude + 0.3)
      }.filter(_._1.id > lastReadEventId)
         .take(30)
         .sortBy(_._1.creationDateMills.desc)
         .result
   }

   def addUserToEvent(eventId: Long, userId: Long) = {
      eventToUserTable += EventToUser(eventId, userId)
   }

   def deleteUserFromEvent(userId: Long, eventId: Long) = {
      eventToUserTable.filter { eventToUser =>
         eventToUser.userId === userId && eventToUser.eventId === eventId
      }.delete
   }

   def cancel(userId: Long, eventId: Long)(implicit ec: ExecutionContext) = {
      eventTable
         .filter(_.id === eventId)
         .map(_.ownerId)
         .result
         .head
         .flatMap {
         id =>
            if (id == userId) {
               eventToUserTable.filter { e => e.eventId === eventId }.delete.andThen(
                  eventTable.filter(_.id === eventId).delete
               ).andThen(Sql(id))
            }
            else
               Sql(id)
      }
   }

}
