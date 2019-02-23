package services.database

import java.nio.file.Paths

import com.google.inject.Inject
import javax.imageio.ImageIO
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}
import services.database.traits.HipeImageService
import services.images.traits.ImageWriterService
import services.traits.TokenRepresentation
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class HipeImageServiceImpl @Inject()(
                                       protected val dbConfigProvider: DatabaseConfigProvider,
                                       imageWriterService: ImageWriterService[Future]
                                    )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with HipeImageService[Future] {

   private lazy val hipeImageTable = TableQuery[HipeImageDAO]
   private lazy val eventHipeImageTable = TableQuery[EventHipeImageDAO]
   private lazy val userHipeImageTable = TableQuery[UserHipeImageDAO]
   private lazy val eventTable = TableQuery[EventDAO]

   override def create(eventId: Long, token: TokenRepresentation, picture: MultipartFormData
   .FilePart[Files.TemporaryFile], host: String) = {

      val filename = Paths.get(picture.filename).getFileName
      val formatName = filename.toString.split("\\.")(1)
      val imageIO = ImageIO.read(picture.ref)
      imageWriterService.write(eventId, token, formatName, imageIO, host).flatMap {
         image =>
            db.run(insertImageQuery(image,eventId))
      }

   }

   override def delete(id: Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter(_.id === id).delete)
   }

   override def findById(id: Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter(_.id === id).result.head)
   }

   override def findByEventId(eventId: Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter { i => i.id in eventHipeImageTable.filter(_.eventId === eventId).map(_.imageId) }
         .sortBy(_.creationMills.desc).result)
   }

   override def findByUserId(userId: Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter {
         i =>
            i.id in userHipeImageTable.filter(_.userId === userId).map(_.imageId)
      }.sortBy(_.creationMills.desc).result)
   }

   private val insertImageQuery = { (image: HipeImage, eventId: Long) =>
      val query = hipeImageTable returning hipeImageTable.map(_.id)
      val execute = eventTable.filter(_.id === eventId).result.head.zip(query += image).flatMap {
         eventWithImageID =>
            eventTable.update(eventWithImageID._1.copy(eventImageId = eventWithImageID._2)).map { _ =>
               image.copy(id = eventWithImageID._2)
            }
      }
      execute
   }

}