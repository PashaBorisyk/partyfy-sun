package services.database

import dao.traits.EventDAO
import implicits.implicits._
import javax.inject.Inject
import models.persistient._
import services.database.traits.EventService
import services.traits.{EventMessagePublisherService, TokenRepresentation}

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl @Inject()(
                                   val eventMessagePublisherService: EventMessagePublisherService,
                                   eventDAO:EventDAO[Future]
                                )(implicit ec:ExecutionContext)extends EventService[Future] {

   override def getEventById(id: Long,token:TokenRepresentation)  = {
      eventDAO.getEventById(id).map{eventImageSeq =>
         eventImageSeq.extractOptions.toArray
      }
   }

   override def delete(id: Long,token:TokenRepresentation)  = {
      eventDAO.deleteById(id)
   }

   override def create(event: (Event, Set[Long]),token:TokenRepresentation) = {
      eventDAO.create(event)
   }

   override def update(event: Event,token:TokenRepresentation)  = {
      eventDAO.update(event)
   }

   override def getEventsByOwner(userId: Long,token:TokenRepresentation)  = {
      eventDAO.getEventsByOwner(userId).map{ eventWithImageSeq =>
         eventWithImageSeq.extractOptions.toArray
      }
   }

   override def getEventsByMemberId(userId: Long,token:TokenRepresentation)  = {
      eventDAO.getEventsByMemberId(userId).map { eventWithImageSeq =>
         eventWithImageSeq.extractOptions.toArray
      }
   }

   override def getEventIdsByMemberId(userId: Long,token:TokenRepresentation) = {
      eventDAO.getEventIdsByMemberId(userId).map(_.toArray)
   }

   override def getEvents(token:TokenRepresentation,latitude: Double, longtitude: Double, lastReadEventId: Long)  = {
      eventDAO.getEvents(token.userId,latitude,longtitude,lastReadEventId).map{ eventWithImagesSeq =>
         eventWithImagesSeq.extractOptions.toArray
      }
   }

   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long,token:TokenRepresentation)  = {
      eventDAO.addMemberToEvent(token.userId,eventId,advancedUserId)
   }

   override def cancelEvent(userId: Long, eventId: Long,token:TokenRepresentation)  = {
      eventDAO.cancelEvent(token.userId,eventId)
   }

   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long,token:TokenRepresentation)  = {
      eventDAO.removeMember(token.userId,advancedUserId,eventId)
   }

   override def test(): Unit = {
      eventMessagePublisherService !+ Event()
   }

}