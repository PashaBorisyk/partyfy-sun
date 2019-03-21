package services.database

import java.nio.file.Paths

import com.google.inject.Inject
import dao.traits.ImageDAO
import javax.imageio.ImageIO
import models.TokenRepPrivate
import models.persistient.UserToImage
import play.api.Logger
import play.api.libs.Files
import play.api.mvc.{MultipartFormData}
import services.database.traits.ImageService
import services.images.traits.ImageWriterService

import scala.concurrent.{ExecutionContext, Future}

class ImageServiceImpl @Inject()(
                                       imageWriterService: ImageWriterService[Future],
                                       imageDAO: ImageDAO[Future]
                                    )(implicit ec: ExecutionContext)
   extends ImageService[Future] {

   private final val logger = Logger(this.getClass)

   override def create(eventId: Long, picture: MultipartFormData
   .FilePart[Files.TemporaryFile], host: String)(implicit token: TokenRepPrivate) = {

      val (imageIO,formatName) = getImageWithName(picture)
      imageWriterService.write(eventId, token, formatName, imageIO, host).flatMap {
         image =>
            imageDAO
               .create(eventId,image)
               .zip(imageDAO.attachToUser(UserToImage(token.userId,image.id)))
      }.map{
         case (image,_) => image
      }
   }

   private def getImageWithName(picture: MultipartFormData.FilePart[Files.TemporaryFile]) ={

      try {
         val filename = Paths.get(picture.filename).getFileName
         val formatName = filename.toString.split("\\.")(1)
         val imageIO = ImageIO.read(picture.ref)
         imageIO->formatName
      }catch {
         case e:IndexOutOfBoundsException =>
            logger.debug("Filename must have format after '.'")
            throw e
      }
   }

   override def delete(id: Long)(implicit token: TokenRepPrivate) = {
      imageDAO.delete(id)
   }

   override def findById(id: Long)(implicit token: TokenRepPrivate) = {
      imageDAO.getById(id)
   }

   override def findByEventId(eventId: Long)(implicit token: TokenRepPrivate) = {
      imageDAO.findByEventId(eventId)
   }

   override def findByUserId(userId: Long)(implicit token: TokenRepPrivate) = {
      imageDAO.findByUserId(userId)
   }

}