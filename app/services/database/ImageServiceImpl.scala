package services.database

import java.nio.file.Paths

import com.google.inject.Inject
import dao.traits.ImageDAO
import javax.imageio.ImageIO
import models.TokenRepPrivate
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}
import services.database.traits.ImageService
import services.images.traits.ImageWriterService

import scala.concurrent.{ExecutionContext, Future}

class ImageServiceImpl @Inject()(
                                       imageWriterService: ImageWriterService[Future],
                                       imageDAO: ImageDAO[Future]
                                    )(implicit ec: ExecutionContext)
   extends ImageService[Future] {

   override def create(eventId: Long, token: TokenRepPrivate, picture: MultipartFormData
   .FilePart[Files.TemporaryFile], host: String) = {

      val filename = Paths.get(picture.filename).getFileName
      val formatName = filename.toString.split("\\.")(1)
      val imageIO = ImageIO.read(picture.ref)
      imageWriterService.write(eventId, token, formatName, imageIO, host).flatMap {
         image => imageDAO.create(eventId,image)
      }
   }

   override def delete(id: Long)(implicit request: Request[_]) = {
      imageDAO.delete(id)
   }

   override def findById(id: Long)(implicit request: Request[_]) = {
      imageDAO.findById(id)
   }

   override def findByEventId(eventId: Long)(implicit request: Request[_]) = {
      imageDAO.findByEventId(eventId)
   }

   override def findByUserId(userId: Long)(implicit request: Request[_]) = {
      imageDAO.findByUserId(userId)
   }

}