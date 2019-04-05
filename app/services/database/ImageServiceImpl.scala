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

class ImageServiceImpl @Inject()(
                                   @Named("kafka-producer") imageProducer: ActorRef,
                                   imageWriterService: ImageWriterService[Future],
                                   imageDAO: ImageDAO[Future])(implicit ec: ExecutionContext)
   extends ImageService[Future] {

   private final val logger = Logger("application")

   override def create(eventID: Long,
                       picture: MultipartFormData.FilePart[Files.TemporaryFile],
                       host: String)(implicit token: TokenRepPrivate) = {

      val (imageIO, formatName) = getImageWithName(picture)
      val createAction = imageWriterService
         .write(eventID, token, formatName, imageIO, host)
         .flatMap { image =>
            imageDAO.create(image)
         }
         .flatMap { image =>
            val usersToImage =
               Array(UserToImage(token.userID, image.id, markerId = token.userID))
            imageDAO
               .attachUserToImage(usersToImage)
               .map(_ => usersToImage)
               .zip(Future.successful(image))
         }
      createAction.onComplete {
         case Success((usersToImage, image)) =>
            imageProducer ! ImageAddedRecord(
               userID = token.userID,
               username = token.username,
               imageID = usersToImage.head.imageID,
               eventID = image.eventID,
               markedUsers = usersToImage.map(_.userID)
            )

      }
      createAction.map(_._2)
   }

   private def getImageWithName(
                                  picture: MultipartFormData.FilePart[Files.TemporaryFile]) = {

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

   override def findByeventID(eventID: Long)(implicit token: TokenRepPrivate) = {
      imageDAO.findByeventID(eventID)
   }

   override def findByuserID(userID: Int)(implicit token: TokenRepPrivate) = {
      imageDAO.findByuserID(userID)
   }

   override def attachUsersToImage(userToImage: Array[UserToImage])(
      implicit token: TokenRepPrivate) = {
      val attachAction = imageDAO.attachUserToImage(userToImage)
      attachAction.onComplete {
         case Success(Some(insertedRows))
            if insertedRows != 0 && userToImage.nonEmpty =>
            imageProducer ! ImageUserAttachedRecord(
               token.userID,
               token.username,
               userToImage.head.imageID,
               userToImage.map(_.userID)
            )
      }
      attachAction
   }

}
