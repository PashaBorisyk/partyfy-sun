package services.database

import implicits.implicits._
import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
import services.database.traits.EventService
import services.traits.EventMessagePublisherService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import util._

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl @Inject()(
                                   val eventMessagePublisherService: EventMessagePublisherService,
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with EventService[Future] {

   val eventTable = TableQuery[EventDAO]
   val userEventTable = TableQuery[EventUserDAO]
   val imageTable = TableQuery[HipeImageDAO]

   override def getEventById(id: Long)(implicit request: Request[_]) = {
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

   override def delete(id: Long)(implicit request: Request[_]) = {
      db.run(eventTable.filter(_.id === id).delete)
   }

   override def create(event: (Event, Set[Long]))(implicit request: Request[_]) = {

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

   override def update(event: Event)(implicit request: Request[_]) = {
      db.run(eventTable.insertOrUpdate(event))
   }

   override def getEventsByOwner(userId: Long)(implicit request: Request[_]) = {

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

   override def getEventsByMemberId(userId: Long)(implicit request: Request[_]) = {

      val execute: DBIOAction[Array[(models.Event, Product with Serializable)], slick.dbio.NoStream, Nothing] = (for {
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

   override def getEventIdsByMemberId(userId: Long)(implicit request: Request[_]) = {
      db.run(eventTable.filter { e =>
         e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId)
      }.map(_.id).result.map(_.toArray))
   }

   override def getEventIdsByMemberIdForSocket(userId: Long)(implicit request: Request[_]) = {
      db.run(eventTable.filter { e => e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }.map
      (_.id).result.map(_.toArray))
   }

   override def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long)(implicit request: Request[_]) = {

      val execute: DBIOAction[Array[(models.Event, Product with Serializable)], slick.dbio.NoStream, Nothing] = (for {
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

   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long)(implicit request: Request[_]) = {
      db.run(userEventTable += EventUser(eventId, advancedUserId))
   }

   override def cancelEvent(userId: Long, eventId: Long)(implicit request: Request[_]) = {

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

   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long)(implicit request: Request[_]) = {

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