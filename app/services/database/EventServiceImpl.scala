package services.database

import implicits.implicits._
import javax.inject.Inject
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.database.traits.EventService
import services.traits.{EventMessagePublisherService, TokenRepresentation}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl @Inject()(
                                   val eventMessagePublisherService: EventMessagePublisherService,
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with EventService[Future] {

   private val eventTable = TableQuery[EventDAO]
   private val userEventTable = TableQuery[EventUserDAO]
   private val imageTable = TableQuery[HipeImageDAO]

   override def getEventById(id: Long,token:TokenRepresentation)  = {
      val query = (for {
         (event, image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event, image)).filter(_._1.id === id)

      db.run(query.result).map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }.toArray
      }

   }

   override def delete(id: Long,token:TokenRepresentation)  = {
      db.run(eventTable.filter(_.id === id).delete)
   }

   override def create(event: (Event, Set[Long]),token:TokenRepresentation)  = {

      val query = eventTable returning eventTable.map(s => s.id)
      val execute = (query += event._1).flatMap { eventId =>

         val eventUserConnections = event._2.map { userId =>
            val execute: DBIOAction[_, slick.dbio.NoStream, Effect.All] = userEventTable += EventUser(eventId, userId)
            execute
         }

         DBIO.seq(eventUserConnections.toArray: _*).map { _ =>
            eventId
         }
      }

      db.run(execute)
   }

   override def update(event: Event,token:TokenRepresentation)  = {
      db.run(eventTable.insertOrUpdate(event))
   }

   override def getEventsByOwner(userId: Long,token:TokenRepresentation)  = {

      val query = (for {
         (event, image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event, image)).filter(_._1.creatorId === userId).sortBy(_._1.id)

      db.run(query.result).map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }.toArray
      }

   }

   override def getEventsByMemberId(userId: Long,token:TokenRepresentation)  = {

      val execute: DBIOAction[Array[(Event, Product with Serializable)], slick.dbio.NoStream, Nothing] = (for {
         (event, image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event, image)).filter { e => e._1.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }.result.map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }.toArray
      }

      db.run(execute)

   }

   override def getEventIdsByMemberId(userId: Long,token:TokenRepresentation)  = {
      db.run(eventTable.filter { e =>
         e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId)
      }.map(_.id).result.map(_.toArray))
   }

   override def getEventIdsByMemberIdForSocket(userId: Long,token:TokenRepresentation)  = {
      db.run(eventTable.filter { e => e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }.map
      (_.id).result.map(_.toArray))
   }

   override def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long,token:TokenRepresentation)  = {

      val execute: DBIOAction[Array[(Event, Product with Serializable)], slick.dbio.NoStream, Nothing] = (for {
         (event, image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event, image)).filter {
         entry =>
            entry._1.isPublic === true && entry._1.latitude > (latitude - 0.3) && entry._1.latitude < (latitude + 0.3)
      }.filter {
         entry =>
            entry._1.longtitude > (longtitude - 0.3) && entry._1.longtitude < (longtitude + 0.3)
      }.filter(_._1.id > lastReadEventId).take(30).sortBy(_._1.id.desc).result.map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }.toArray
      }

      db.run(execute)

   }

   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long,token:TokenRepresentation)  = {
      db.run(userEventTable += EventUser(eventId, advancedUserId))
   }

   override def cancelEvent(userId: Long, eventId: Long,token:TokenRepresentation)  = {

      val execute = eventTable.filter(_.id === eventId).map(_.creatorId).result.head.map {
         id =>
            if (id == userId) {
               userEventTable.filter { e => e.eventId === eventId }.delete.andThen(
                  eventTable.filter(_.id === eventId).delete
               )
            }
            id
      }
      db.run(execute)

   }

   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long,token:TokenRepresentation)  = {

      val execute = if (userId == advancedUserId)
         userEventTable.filter { e => e.eventId === eventId && e.userId === userId }
            .delete
      else
         eventTable.filter(_.id === eventId).map(_.creatorId).result.head.map {
            id =>
               if (id == userId) {
                  userEventTable.filter { e => e.eventId === eventId && e.userId === advancedUserId }.delete
               }
         }

      db.run(execute)
   }

   override def test(): Unit = {
      eventMessagePublisherService !+ Event()
   }

}