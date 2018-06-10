package controllers.rest

import db.services.interfaces.ChatMessageService
import db.services.{EventServiceImpl, UserServiceImpl}
import implicits.implicits._
import javax.inject.Inject
import models.Event
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.traits.EventMessagePublisherService
import util._

import scala.concurrent.ExecutionContext

class EventControllerImpl @Inject()(
                                      cc: ControllerComponents,
                                      val eventMessagePublisherService: EventMessagePublisherService,
                                      val eventService: EventServiceImpl,
                                      val userService: UserServiceImpl,
                                      val chats: ChatMessageService
                                   )(implicit ec: ExecutionContext)
   extends AbstractController(cc) {
   
   def createEvent(): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString())
         eventService.create(req.body).map {
            result =>
               eventMessagePublisherService.publishCreated(result)
               Created(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def updateEvent(): Action[AnyContent] = Action.async {
      req =>
         eventService.update(req.body).map {
            result =>
               eventMessagePublisherService.publishUpdated[(Event,Event)](
                  Const.MSG_INSTANCE_OF_EVENT,
                  req.body->
               )
               Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def getById(id: Long): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString)
         eventMessagePublisherService.publishCreated[Event](Event())
         eventService.getEventById(id).map {
            e => Ok(e.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def getByOwner(userId: Long): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString())
         eventService.getEventsByOwner(userId).map {
            result => if (result.nonEmpty) Ok(result.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def getByMember(userId: Long): Action[AnyContent] = Action.async {
      req =>
         
         logger.debug(req.toString())
         eventService.getEventsByMemberId(userId).map {
            result =>  if (result.nonEmpty) Ok(result.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString())
         eventService.getEvents(userId, latitude, longtitude, lastReadEventId).map {
            result => logger.debug(result.toArray.toJson); Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def cancelEvent(userId: Long, eventId: Long): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString())
         eventService.cancelEvent(userId, eventId).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def removeMember(userId: Long, advancedUserId: Long, eventId: Long): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString())
         eventService.removeMember(userId, advancedUserId, eventId).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long): Action[AnyContent] = Action.async {
      req =>
         logger.debug(req.toString())
         eventService.addMemberToEvent(eventId, userId, advancedUserId).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
}