package dao.sql

import implicits.implicits._
import models.persistient._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

object EventSql {

   private val eventTable = TableQuery[EventTable]
   private val eventToUserTable = TableQuery[EventToUserTable]
   private val imageTable = TableQuery[ImageTable]

   def deleteById(id: Long) = {
      eventTable.filter(_.id === id).delete
   }

   def create(event: (Event, Set[Long]))(implicit ec: ExecutionContext) = {
      val insertQuery = eventTable returning eventTable.map(table => table.id)
      val execute  = (insertQuery += event._1).flatMap { eventId =>

         val eventUserConnections = event._2.map { userId =>
//            val execute: DBIOAction[_, slick.dbio.NoStream, Effect.Write] =
//            execute
               eventToUserTable += EventToUser(eventId, userId)
         }

         DBIO.seq(eventUserConnections.toArray: _*).map { _ =>
            eventId
         }
      }

      execute
   }

   def update(event: Event) = {
      eventTable.update(event)
   }

   def getById(id: Long) = {
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter(_._1.id === id).result
   }

   def getByOwnerId(userId: Long) = {
      (eventTable joinLeft imageTable on (_.eventImageId === _.id)).filter(_._1.creatorId === userId).sortBy(_._1.id)
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
         eventAndImage =>
            eventAndImage._1.isPublic === true && eventAndImage._1.latitude > (latitude - 0.3) && eventAndImage._1.latitude < (latitude + 0.3)
      }.filter {
         eventAndImage =>
            eventAndImage._1.longtitude > (longtitude - 0.3) && eventAndImage._1.longtitude < (longtitude + 0.3)
      }.filter(_._1.id > lastReadEventId).take(30).sortBy(_._1.id.desc).result
   }

   def addUserToEvent(eventId: Long, userId: Long) = {
      eventToUserTable += EventToUser(eventId, userId)
   }

   def deleteUserFromEvent(userId: Long, eventId: Long) = {
      eventToUserTable.filter{eventToUser =>
         eventToUser.userId === userId && eventToUser.eventId === eventId
      }.delete
   }

   def cancel(userId:Long,eventId:Long)(implicit ec: ExecutionContext) = {
      eventTable.filter(_.id === eventId).map(_.creatorId).result.head.map {
         id =>
            if (id == userId) {
               eventToUserTable.filter { e => e.eventId === eventId }.delete.andThen(
                  eventTable.filter(_.id === eventId).delete
               )
            }
            id
      }
   }

}
