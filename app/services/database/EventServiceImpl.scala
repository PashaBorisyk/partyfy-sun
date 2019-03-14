package services.database

import dao.traits.EventDAO
import implicits._
import javax.inject.Inject
import models.TokenRepPrivate
import models.persistient._
import services.database.traits.EventService
import services.traits.EventMessagePublisherService

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl @Inject()(
                                   val eventMessagePublisherService: EventMessagePublisherService,
                                   eventDAO:EventDAO[Future]
                                )(implicit ec:ExecutionContext)extends EventService[Future] {

   override def getEventById(id: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.getEventById(id)
   }

   override def delete(id: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.deleteById(id)
   }

   override def create(event: (Event, Set[Long]))(implicit token:TokenRepPrivate) = {
      eventDAO.create(event)
   }

   override def update(event: Event)(implicit token:TokenRepPrivate)  = {
      eventDAO.update(event)
   }

   override def getEventsByOwner(userId: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.getEventsByOwner(userId)
   }

   override def getEventsByMemberId(userId: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.getEventsByMemberId(userId)
   }

   override def getEventIdsByMemberId(userId: Long)(implicit token:TokenRepPrivate) = {
      eventDAO.getEventIdsByMemberId(userId)
   }

   override def getEvents(latitude: Double, longtitude: Double, lastReadEventId: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.getEvents(token.userId,latitude,longtitude,lastReadEventId)
   }

   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.addMemberToEvent(token.userId,eventId,advancedUserId)
   }

   override def cancelEvent(userId: Long, eventId: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.cancelEvent(token.userId,eventId)
   }

   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long)(implicit token:TokenRepPrivate)  = {
      eventDAO.removeMember(token.userId,advancedUserId,eventId)
   }

   override def test(): Unit = {
      eventMessagePublisherService !+ Event()
   }

}