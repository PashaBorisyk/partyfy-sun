package services.database

import java.nio.file.Paths

import actors.{ImageAddedRecord, ImageUserAttachedRecord}
import akka.actor.ActorRef
import com.google.inject.Inject
import dao.traits.ImageDAO
import javax.imageio.ImageIO
import javax.inject.Named
import models.TokenRepPrivate
import models.persistient.UserToImage
import play.api.Logger
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import services.database.traits.ImageService
import services.images.traits.ImageWriterService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class ImageServiceImpl @Inject()(@Named("kafka-producer") imageProducer: ActorRef,
                                 imageWriterService: ImageWriterService[Future],
                                 imageDAO: ImageDAO[Future]
                                )(implicit ec: ExecutionContext)
   extends ImageService[Future] {

   private final val logger = Logger("application")

   override def create(eventId: Long, picture: MultipartFormData
   .FilePart[Files.TemporaryFile], host: String)(implicit token: TokenRepPrivate) = {

      val (imageIO, formatName) = getImageWithName(picture)
      val createAction = imageWriterService.write(eventId, token, formatName, imageIO, host).flatMap {
         image =>
            imageDAO.create(image, Array(UserToImage(token.userId, image.id, isMarked = false)))
      }
      createAction.onComplete {
         case Success((image, _)) =>
            imageProducer ! ImageAddedRecord(image.ownerId, token.username, image.id, image.eventId, Array(image.ownerId))

      }
      createAction.map(_._1)
   }

   private def getImageWithName(picture: MultipartFormData.FilePart[Files.TemporaryFile]) = {

      try {
         val filename = Paths.get(picture.filename).getFileName
         val formatName = filename.toString.split("\\.")(1)
         val imageIO = ImageIO.read(picture.ref)
         imageIO -> formatName
      } catch {
         case e: IndexOutOfBoundsException =>
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

   override def findByUserId(userId: Int)(implicit token: TokenRepPrivate) = {
      imageDAO.findByUserId(userId)
   }

   override def attachUsersToImage(userToImage: Array[UserToImage])(implicit token: TokenRepPrivate) = {
      val attachAction = imageDAO.attachToUser(userToImage)
      attachAction.onComplete {
         case Success(Some(insertedRows)) if insertedRows != 0 && userToImage.nonEmpty =>
            imageProducer ! ImageUserAttachedRecord(
               token.userId, token.username,
               userToImage.head.imageId, userToImage.map(_.userId)
            )
      }
      attachAction
   }

}