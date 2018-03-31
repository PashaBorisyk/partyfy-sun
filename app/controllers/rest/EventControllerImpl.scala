package controllers.rest

import play.api.mvc.{AbstractController, ControllerComponents}
import javax.inject.Inject

import db.services.{ChatMessageService, EventService, UserService}
import implicits.implicits._
import util._

import scala.concurrent.ExecutionContext

class EventControllerImpl @Inject()(
                                      cc: ControllerComponents,
                                      val eventService: EventService,
                                      val userService: UserService,
                                      val chats:ChatMessageService
                                   )(implicit ec: ExecutionContext)
   extends AbstractController(cc) {
   
   def createEvent() = Action.async {
      req =>
         logger.info(req.toString())
         eventService.create(req.body).map {
            result => Created(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
   def updateEvent() = Action.async {
      req =>
         eventService.update(req.body).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
   def getById(id: Long) = Action.async {
      req =>
         logger.info(req.toString)
         eventService.getEventById(id).map {
            e => Ok(e.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def getByOwner(userId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         eventService.getEventsByOwner(userId).map {
            result => if (result.nonEmpty) Ok(result.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def getByMember(userId: Long) = Action.async {
      req =>
         
         logger.info(req.toString())
         eventService.getEventsByMemberId(userId).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         eventService.getEvents(userId, latitude, longtitude, lastReadEventId).map {
            result => logger.info(result.toArray.toJson);Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def cancelEvent(userId: Long, eventId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         eventService.cancelEvent(userId, eventId).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
   def removeMember(userId: Long, advancedUserId: Long, eventId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         eventService.removeMember(userId, advancedUserId, eventId).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
   def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         eventService.addMemberToEvent(eventId, userId, advancedUserId).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
      
   }
   
}