package controllers.rest

import controllers.rest.implicits.getToken
import javax.inject.Inject
import models.persistient.implicits._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.database.ImageServiceImpl
import services.traits.JWTCoder
import util._

import scala.concurrent.{ExecutionContext, Future}

class ImageController @Inject()(
                                  cc: ControllerComponents,
                                  private val imageService: ImageServiceImpl
                               )(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) {

   private val logger = Logger("application")

   def upload(eventId: Long) = Action.async(parse.multipartFormData) {
      implicit request =>
         logger.debug(request.toString())
         implicit val token = getToken

         request.body
            .file(Const.PART_FILE)
            .map { picture =>
               imageService.create(eventId, picture, request.host).map { image =>
                  Created(Json.toJson(image))
               }

            }
            .getOrElse(Future.successful(
               BadRequest(s"No image part with name ${Const.PART_FILE} found")
            ))

   }

   def get(id: Long) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      imageService.findById(id).map { image =>
         if (image.isEmpty)
            NoContent
         else
            Ok(Json.toJson(image))
      }
   }

   def delete(id: Long) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      imageService.delete(id).map { deletedRows =>
         Accepted(deletedRows.toString)
      }
   }

   def getByEventId(eventId: Long) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      imageService.findByEventId(eventId).map { images =>
         if (images.nonEmpty)
            Ok(Json.toJson(images))
         else
            NoContent
      }
   }

   def getByUserId(userId: Int) = Action.async { implicit req =>
      logger.debug(req.toString())
      implicit val token = getToken
      imageService.findByUserId(userId).map { images =>
         if (images.nonEmpty)
            Ok(Json.toJson(images))
         else
            NoContent
      }
   }

}
