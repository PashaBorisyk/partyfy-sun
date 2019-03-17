package controllers.rest

import controllers.rest.implicits.getToken
import models.persistient.implicits._
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.database.ImageServiceImpl
import services.traits.JWTCoder
import util._

import scala.concurrent.{ExecutionContext, Future}

class ImageController @Inject()(
                                  cc: ControllerComponents,
                                  private val hipeImageService: ImageServiceImpl
                               )(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) {

   private val logger = Logger(this.getClass)

   def upload(eventId: Long) = Action.async(parse.multipartFormData) {
      implicit request =>
         logger.debug(request.toString())
         implicit val token = getToken

         request.body.file(Const.PART_FILE).map { picture =>
            hipeImageService.create(eventId,getToken, picture,request.host).map {
               image => Created(Json.toJson(image))
            }

         }.getOrElse(Future {
            BadRequest(s"No image part with name ${Const.PART_FILE} found")
         })

   }

   def get(id: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         hipeImageService.findById(id).map {
            image => Ok(Json.toJson(image))
         }
   }

   def delete(id: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         hipeImageService.delete(id).map {
            deletedRows => Accepted(deletedRows.toString)
         }
   }

   def getByEventId(eventId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         hipeImageService.findByEventId(eventId).map {
            images => if (images.nonEmpty) Ok(Json.toJson(images)) else NoContent
         }
   }

   def getByUserId(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         implicit val token = getToken
         hipeImageService.findByUserId(userId).map {

            images => if (images.nonEmpty) Ok(Json.toJson(images)) else NoContent
         }
   }

}