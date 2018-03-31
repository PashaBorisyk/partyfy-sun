package controllers.rest

import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.inject.Inject

import db.services.HipeImageService
import models.HipeImage
import org.imgscalr.Scalr
import util._
import util.Const._
import play.api.mvc.{AbstractController, ControllerComponents}
import implicits.implicits._
import services.ImageWriterService

import scala.concurrent.{ExecutionContext, Future}

class HipeImageController @Inject()(
                                      cc: ControllerComponents,
                                      private val hipeImageService: HipeImageService
                                   )(implicit ec: ExecutionContext)
   extends AbstractController(cc) {
   
   def upload(userId: Long, eventId: Long) = Action.async(parse.multipartFormData) {
      implicit request =>
         logger.info(request.toString())
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
      req =>
         logger.info(req.toString())
         hipeImageService.get(id).map {
            s => Ok(s.toJson)
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def delete(id: Long) = Action.async {
      req =>
         logger.info(req.toString())
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
      req =>
         logger.info(req.toString())
         hipeImageService.getByEventId(eventId).map {
            s => if (s.nonEmpty) Ok(s.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
   def getByUserId(userId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         hipeImageService.getByUserId(userId).map {
            
            s => if (s.nonEmpty) Ok(s.toArray.toJson) else NoContent
         }.recover {
            case e: Exception => InternalServerError({
               e.printStackTrace()
               e.getMessage
            })
         }
   }
   
}