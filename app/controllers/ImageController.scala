package controllers

import implicits.implicits._
import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import services.database.HipeImageServiceImpl
import services.traits.JWTCoder
import util._

import scala.concurrent.{ExecutionContext, Future}

class ImageController @Inject()(
                                  cc: ControllerComponents,
                                  private val hipeImageService: HipeImageServiceImpl
                               )(implicit ec: ExecutionContext, jwtCoder: JWTCoder)
   extends AbstractController(cc) {

   private val logger = Logger(this.getClass)

   def upload(eventId: Long) = Action.async(parse.multipartFormData) {
      implicit request =>
         logger.debug(request.toString())
         request.body.file(Const.PART_FILE).map { picture =>

            hipeImageService.create(eventId,getToken, picture,request.host).map {
               image => Created(image.toJson)
            }.recover {
               case e: Exception =>
                  e.printStackTrace()
                  InternalServerError(e.getMessage)
            }

         }.getOrElse(Future {
            BadRequest(s"No image part with name ${Const.PART_FILE} found")
         })

   }

   def get(id: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.findById(id).map {
            s => Ok(s.toJson)
         }.recover {
            case e: Exception =>
               logger.debug("Error gerring picture : ", e)
               InternalServerError(e.getMessage)
         }
   }

   def delete(id: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.delete(id).map {
            s => Accepted(s.toJson)
         }.recover {
            case e: Exception =>
               logger.error("Error deleting picture : ", e)
               InternalServerError(e.getMessage)
         }
   }

   def getByEventId(eventId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.findByEventId(eventId).map {
            s => if (s.nonEmpty) Ok(s.toArray.toJson) else NoContent
         }.recover {
            case e: Exception =>
               logger.error("Error getting picture : ", e)
               InternalServerError(e.getMessage)
         }
   }

   def getByUserId(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.findByUserId(userId).map {

            s => if (s.nonEmpty) Ok(s.toArray.toJson) else NoContent
         }.recover {
            case e: Exception =>
               logger.error("Error getting by userId : ", e)
               InternalServerError(e.getMessage)
         }
   }

}