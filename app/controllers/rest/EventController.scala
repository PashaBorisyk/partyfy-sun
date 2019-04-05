package controllers.rest

import controllers.rest.implicits._
import javax.inject.Inject
import models.persistient.Event
import models.persistient.implicits._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.database.UserServiceImpl
import services.database.traits.EventService
import services.traits.JWTCoder

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

class EventController @Inject()(
                                  cc: ControllerComponents,
                                  val eventService: EventService[Future],
                                  val userService: UserServiceImpl,
                               )(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) {

   private val logger = Logger("application")

   def createEvent() = Action.async(parse.json[(Event, Array[Int])]) {
      implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.create(req.body).map { eventID =>
            Created(Json.toJson(eventID))
         }
   }

   def updateEvent() = Action.async(parse.json[Event]) { implicit req =>
      implicit val token = getToken
      eventService.update(req.body).map { rowsUpdated =>
         Ok(rowsUpdated.toString)
      }
   }

   def getById(id: Long) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      eventService.getEventById(id).map { eventWithImage =>
         Ok(Json.toJson(eventWithImage))
      }

   }

   def getByOwner(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.getEventsByOwner(userID).map { eventWithImages =>
         if (eventWithImages.nonEmpty) Ok(Json.toJson(eventWithImages))
         else NoContent
      }

   }

   def getByMember(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.getEventsByMemberId(userID).map { eventsWithImages =>
         if (eventsWithImages.nonEmpty) Ok(Json.toJson(eventsWithImages))
         else NoContent
      }

   }

   def getIDsByMemberID(userID:Int) = Action.async{ implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.getEventIDsByMemberId(userID).map{
         eventsIDs => if(eventsIDs.nonEmpty)
            Ok(Json.toJson(eventsIDs))
         else
            NoContent
      }
   }

   def getEvents(latitude: Double, longtitude: Double, lastReadeventID: Long) =
      Action.async { implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.getEvents(latitude, longtitude, lastReadeventID).map {
            eventsWithImages =>
               Ok(Json.toJson(eventsWithImages))
         }

      }

   def cancelEvent(eventID: Long) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.cancelEvent(eventID).map { creatorId =>
         Ok(creatorId.toString)
      }
   }

   def removeMember(eventID: Long, userID: Int): Action[AnyContent] =
      Action.async { implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.removeUserFromEvent(eventID, userID).map { _ =>
            Accepted
         }

      }

   def addMemberToEvent(eventID: Long, userID: Int): Action[AnyContent] =
      Action.async { implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.addUserToEvent(eventID, userID).map { _ =>
            Accepted
         }

      }

   def test(): Action[AnyContent] = Action.async { implicit req =>
      logger.debug("Test method")
      eventService.test()
      Future {
         Ok("Something")
      }
   }

}
