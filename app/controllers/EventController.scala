package controllers

import implicits.implicits._
import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.database.traits.EventService
import services.database.{EventServiceImpl, UserServiceImpl}
import services.traits.{EventMessagePublisherService, JWTCoder}
import util._

import scala.concurrent.{ExecutionContext, Future}

class EventController @Inject()(
                                      cc: ControllerComponents,
                                      val eventMessagePublisherService: EventMessagePublisherService,
                                      val eventService: EventService[Future],
                                      val userService: UserServiceImpl,
                                   )(implicit ec: ExecutionContext,JWTCoder: JWTCoder)
   extends AbstractController(cc) {

   private val logger = Logger(this.getClass)

   def createEvent(): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug(req.toString())
         eventService.create(req.body,getToken).map {
            eventId =>
               eventMessagePublisherService ! eventId -> req.body._2
               Created(eventId.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def updateEvent(): Action[AnyContent] = Action.async {
      implicit req=>
         eventService.update(req.body,getToken).map {
            result =>
               eventMessagePublisherService ! (
                  Const.MSG_INSTANCE_OF_EVENT, req.body._1
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
      implicit req=>
         logger.debug(req.toString)
         eventService.getEventById(id,getToken).map {
            e => Ok(e.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def getByOwner(userId: Long): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug(req.toString())
         eventService.getEventsByOwner(userId,getToken).map {
            result => if (result.nonEmpty) Ok(result.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def getByMember(userId: Long): Action[AnyContent] = Action.async {
      implicit req =>
         
         logger.debug(req.toString())
         eventService.getEventsByMemberId(userId,getToken).map {
            result => if (result.nonEmpty) Ok(result.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug(req.toString())
         eventService.getEvents(userId, latitude, longtitude, lastReadEventId,getToken).map {
            result =>
               logger.debug(result.toJson)
               Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def cancelEvent(userId: Long, eventId: Long): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug(req.toString())
         eventService.cancelEvent(userId, eventId,getToken).map {
            result =>
               eventMessagePublisherService !- (userId, eventId)
               Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def removeMember(userId: Long, advancedUserId: Long, eventId: Long): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug(req.toString())
         eventService.removeMember(userId, advancedUserId, eventId,getToken).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }
   
   def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug(req.toString())
         eventService.addMemberToEvent(eventId, userId, advancedUserId,getToken).map {
            result => Ok(result.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
      
   }

   def test(): Action[AnyContent] = Action.async {
      implicit req =>
         logger.debug("Test method")
         eventService.test()
         Future {
            Ok("Something")
         }
   }
   
}