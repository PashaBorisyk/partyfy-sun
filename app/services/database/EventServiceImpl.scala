package services.database

import actors._
import akka.actor.ActorRef
import dao.traits.EventDAO
import javax.inject.{Inject, Named}
import models.TokenRepPrivate
import models.persistient._
import services.database.traits.EventService

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl @Inject()(
                                   @Named("kafka-producer") eventPublisher: ActorRef,
                                   eventDAO: EventDAO[Future])(implicit ec: ExecutionContext)
   extends EventService[Future] {

   override def getEventById(id: Long)(implicit token: TokenRepPrivate) = {
      eventDAO.getEventById(id)
   }

   override def delete(id: Long)(implicit token: TokenRepPrivate) = {
      val deleteAction = eventDAO.deleteById(id)
      deleteAction.onComplete { deletedRows =>
         if (deletedRows.getOrElse(0) != 0)
            eventPublisher ! EventDeletedRecord(token.userId, token.username, id)
      }
      deleteAction
   }

   override def create(eventWithUsersIds: (Event, Array[Int]))(
      implicit token: TokenRepPrivate) = {
      val (event, usersIds) = eventWithUsersIds
      val createAction = eventDAO.create(writeEventFields(event), usersIds)
      createAction.onComplete { eventId =>
         if (eventId.getOrElse(0) != 0)
            eventPublisher ! EventCreatedRecord(token.userId,
               token.username,
               eventId.get,
               usersIds)
      }
      createAction
   }

   override def update(event: Event)(implicit token: TokenRepPrivate) = {
      val updateAction = eventDAO.update(event)
      updateAction.onComplete { updatedRows =>
         if (updatedRows.getOrElse(0) != 0)
            eventPublisher ! EventUpdatedRecord(token.userId,
               token.username,
               event.id)
      }
      updateAction
   }

   override def getEventsByOwner(userId: Int)(
      implicit token: TokenRepPrivate) = {
      eventDAO.getEventsByOwner(userId)
   }

   override def getEventsByMemberId(userId: Int)(
      implicit token: TokenRepPrivate) = {
      eventDAO.getEventsByMemberId(userId)
   }

   override def getEventIdsByMemberId(userId: Int)(
      implicit token: TokenRepPrivate) = {
      eventDAO.getEventIdsByMemberId(userId)
   }

   override def getEvents(
                            latitude: Double,
                            longtitude: Double,
                            lastReadEventId: Long)(implicit token: TokenRepPrivate) = {
      eventDAO.getEvents(token.userId, latitude, longtitude, lastReadEventId)
   }

   override def addUserToEvent(eventId: Long, userId: Int)(
      implicit token: TokenRepPrivate) = {
      val addUserAction = eventDAO.addUserToEvent(eventId, userId)
      addUserAction.onComplete { insertedRows =>
         if (insertedRows.getOrElse(0) != 0) {
            eventPublisher ! EventUserAddedRecord(token.userId,
               token.username,
               eventId,
               userId)
         }
      }
      addUserAction
   }

   override def cancelEvent(eventId: Long)(implicit token: TokenRepPrivate) = {
      eventDAO.cancelEvent(eventId, token.userId)
   }

   override def removeUserFromEvent(eventId: Long, userId: Int)(
      implicit token: TokenRepPrivate) = {
      val removeUserAction = eventDAO.removeUserFromEvent(eventId, token.userId)
      removeUserAction.onComplete { deletedRows =>
         if (deletedRows.getOrElse(0) != 0) {
            eventPublisher ! EventUserRemovedRecord(token.userId,
               token.username,
               eventId,
               userId)
         }
      }
      removeUserAction
   }

   override def test(): Unit = {}

   private final def writeEventFields(event: Event)(
      implicit token: TokenRepPrivate) = {
      event.copy(ownerId = token.userId, ownerUsername = token.username)
   }

}
