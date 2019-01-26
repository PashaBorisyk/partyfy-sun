package db.services


import db.services.interfaces.EventService
import implicits.implicits._
import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
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
      logger.debug(request.username)
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
            }
      }

   }

   override def delete(id: Long)(implicit request: Request[_]) = {
      val result = db.run(eventTable.filter(_.id === id).delete)
      result
   }

   override def create(event: (Event, Set[Long]))(implicit request: Request[_]) = {
      val query = eventTable returning eventTable.map(s => s)
      val eventFuture: Future[Event] = db.run(query += event._1)
      val result = eventFuture.map { createdEvent =>

         event._2.foreach { id =>
            db.run(userEventTable += EventUser(createdEvent.id, id))
         }
         createdEvent -> event._2

      }
      result
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
            }
      }

   }

   override def getEventsByMemberId(userId: Long)(implicit request: Request[_]) = {

      val query = (for {
         (event, image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event, image)).filter { e => e._1.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }

      db.run(query.result).map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }
      }

   }

   override def getEventIdsByMemberId(userId: Long)(implicit request: Request[_]) = {
      db.run(eventTable.filter { e => e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }.map(_.id).result)
   }

   override def getEventIdsByMemberIdForSocket(userId: Long)(implicit request: Request[_]) = {
      db.run(eventTable.filter { e => e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }.map(_.id).result)
   }

   override def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long)(implicit request: Request[_]) = {

      val query = (for {
         (event, image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event, image)).filter {
         entry =>
            entry._1.isPublic === true && entry._1.latitude > (latitude - 0.3) && entry._1.latitude < (latitude + 0.3)
      }.filter {
         entry =>
            entry._1.longtitude > (longtitude - 0.3) && entry._1.longtitude < (longtitude + 0.3)
      }.filter(_._1.id > lastReadEventId).take(30).sortBy(_._1.id.desc)

      db.run(query.result).map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }
      }
   }

   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long)(implicit request: Request[_]) = {
      db.run(userEventTable += EventUser(eventId, advancedUserId))
   }

   override def cancelEvent(userId: Long, eventId: Long)(implicit request: Request[_]) = {

      db.run(eventTable.filter(_.id === eventId).map(_.creatorId).result.head).map {
         id =>
            if (id == userId) {
               db.run(userEventTable.filter { e => e.eventId === eventId }.delete)
               db.run(eventTable.filter(_.id === eventId).delete)
            }
      }

   }

   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long)(implicit request: Request[_]) = {

      if (userId == advancedUserId)
         db.run(userEventTable.filter { e => e.eventId === eventId && e.userId === userId }.delete)
      else
         db.run(eventTable.filter(_.id === eventId).map(_.creatorId).result.head).map {
            id =>
               if (id == userId) {
                  db.run(userEventTable.filter { e => e.eventId === eventId && e.userId === advancedUserId }.delete)
               }
         }

   }

   override def test(): Unit = {
      eventMessagePublisherService !+ Event()
   }

}