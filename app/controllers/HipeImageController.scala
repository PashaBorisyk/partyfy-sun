package controllers

import java.nio.file.Paths

import implicits.implicits._
import javax.imageio.ImageIO
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ImageWriterService
import services.database.HipeImageServiceImpl
import util._

import scala.concurrent.{ExecutionContext, Future}

class HipeImageController @Inject()(
                                      cc: ControllerComponents,
                                      private val hipeImageService: HipeImageServiceImpl
                                   )(implicit ec: ExecutionContext)
   extends AbstractController(cc) {
   
   def upload(userId: Long, eventId: Long) = Action.async(parse.multipartFormData) {
      implicit request =>
         logger.debug(request.toString())
         request.body.file(Const.PART_FILE).map { picture =>
            
            val filename = Paths.get(picture.filename).getFileName
            val formatName = filename.toString.split("\\.")(1)
            val imageIO = ImageIO.read(picture.ref)
            
            val hipeImage = ImageWriterService.write(eventId,userId,formatName,imageIO)
            
            hipeImageService.create(eventId,hipeImage).map{
               s => Created(s.toJson)
            }.recover {
               case e: Exception => InternalServerError({
                  e.printStackTrace()
                  e.getMessage
               })
            }
            
         }.getOrElse(Future{BadRequest("No image part with name 'part-file' found")})
      
   }
   
   def get(id: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.findById(id).map {
            s => Ok(s.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def delete(id: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.delete(id).map {
            s => Accepted(s.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def getByEventId(eventId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.findByEventId(eventId).map {
            s => if (s.nonEmpty) Ok(s.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def getByUserId(userId: Long) = Action.async {
      implicit req =>
         logger.debug(req.toString())
         hipeImageService.findByUserId(userId).map {
            
            s => if (s.nonEmpty) Ok(s.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
}