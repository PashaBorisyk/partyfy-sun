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
            eventPublisher ! EventDeletedRecord(token.userID, token.username, id)
      }
      deleteAction
   }

   override def create(eventWithUsersIDs: (Event, Array[Int]))(
      implicit token: TokenRepPrivate) = {
      val (event, usersIDss) = eventWithUsersIDs
      val createAction = eventDAO.create(writeEventFields(event), usersIDss)
      createAction.onComplete { eventID =>
         if (eventID.getOrElse(0) != 0)
            eventPublisher ! EventCreatedRecord(token.userID,
               token.username,
               eventID.get
            )
      }
      createAction
   }

   override def update(event: Event)(implicit token: TokenRepPrivate) = {
      val updateAction = eventDAO.update(event)
      updateAction.onComplete { updatedRows =>
         if (updatedRows.getOrElse(0) != 0)
            eventPublisher ! EventUpdatedRecord(token.userID,
               token.username,
               event.id)
      }
      updateAction
   }

   override def getEventsByOwner(userID: Int)(
      implicit token: TokenRepPrivate) = {
      eventDAO.getEventsByOwner(userID)
   }

   override def getEventsByMemberId(userID: Int)(
      implicit token: TokenRepPrivate) = {
      eventDAO.getEventsByMemberId(userID)
   }

   override def getEventIDsByMemberId(userID: Int)(
      implicit token: TokenRepPrivate) = {
      eventDAO.getEventIDsByMemberID(userID)
   }

   override def getEvents(
                            latitude: Double,
                            longtitude: Double,
                            lastReadeventID: Long)(implicit token: TokenRepPrivate) = {
      eventDAO.getEvents(token.userID, latitude, longtitude, lastReadeventID)
   }

   override def addUserToEvent(eventID: Long, userID: Int)(
      implicit token: TokenRepPrivate) = {
      val addUserAction = eventDAO.addUserToEvent(eventID, userID)
      addUserAction.onComplete { insertedRows =>
         if (insertedRows.getOrElse(0) != 0) {
            eventPublisher ! EventUserAddedRecord(token.userID,
               token.username,
               eventID,
               userID)
         }
      }
      addUserAction
   }

   override def cancelEvent(eventID: Long)(implicit token: TokenRepPrivate) = {
      eventDAO.cancelEvent(eventID, token.userID)
   }

   override def removeUserFromEvent(eventID: Long, userID: Int)(
      implicit token: TokenRepPrivate) = {
      val removeUserAction = eventDAO.removeUserFromEvent(eventID, token.userID)
      removeUserAction.onComplete { deletedRows =>
         if (deletedRows.getOrElse(0) != 0) {
            eventPublisher ! EventUserRemovedRecord(token.userID,
               token.username,
               eventID,
               userID)
         }
      }
      removeUserAction
   }

   override def test(): Unit = {}

   private final def writeEventFields(event: Event)(
      implicit token: TokenRepPrivate) = {
      event.copy(ownerID = token.userID, ownerUsername = token.username)
   }

}
