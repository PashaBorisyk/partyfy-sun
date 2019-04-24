package controllers.rest

import controllers.rest.implicits._
import javax.inject.Inject
import models.implicits._
import models.persistient.Event
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

   def create() = Action.async(parse.json[(Event, Array[Int])]) {
      implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.create(req.body).map { eventID =>
            Created(Json.toJson(eventID))
         }
   }

   def update() = Action.async(parse.json[Event]) { implicit req =>
      implicit val token = getToken
      eventService.update(req.body).map { rowsUpdated =>
         if(rowsUpdated > 0)
            Accepted
         else
            NotModified
      }
   }

   def getByID(id: Long) = Action.async { implicit req =>
      logger.debug(req.toString)
      implicit val token = getToken
      eventService.getEventById(id).map { eventWithImage =>
         if (eventWithImage.isDefined)
            Ok(Json.toJson(eventWithImage))
         else
            NoContent
      }

   }

   def getByOwnerID(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.getEventsByOwner(userID).map { eventWithImages =>
         if (eventWithImages.nonEmpty) Ok(Json.toJson(eventWithImages))
         else NoContent
      }

   }

   def getByUserID(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.getEventsByMemberId(userID).map { eventsWithImages =>
         if (eventsWithImages.nonEmpty) Ok(Json.toJson(eventsWithImages))
         else NoContent
      }

   }

   def getIDsByUserID(userID: Int) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.getEventIDsByMemberId(userID).map {
         eventsIDs =>
            if (eventsIDs.nonEmpty)
               Ok(Json.toJson(eventsIDs))
            else
               NoContent
      }
   }

   def get(latitude: Double, longtitude: Double, lastReadEventID: Long) =
      Action.async { implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.getEvents(latitude, longtitude, lastReadEventID).map {
            eventsWithImages =>
               Ok(Json.toJson(eventsWithImages))
         }

      }

   def cancel(eventID: Long) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      eventService.cancelEvent(eventID).map { updatedRows =>
         if (updatedRows > 0)
            Accepted
         else
            NotModified
      }
   }

   def removeUser(eventID: Long, userID: Int): Action[AnyContent] =
      Action.async { implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.removeUserFromEvent(eventID, userID).map { updatedRows =>
            if(updatedRows > 0)
               Accepted
            else
               NotModified
         }

      }

   def addUser(eventID: Long, userID: Int): Action[AnyContent] =
      Action.async { implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         eventService.addUserToEvent(eventID, userID).map { insertedRows =>
            if(insertedRows > 0)
               Accepted
            else
               NotModified
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
