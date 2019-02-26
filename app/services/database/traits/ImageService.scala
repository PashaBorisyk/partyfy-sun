package services.database.traits

import com.google.inject.ImplementedBy
import models.persistient.Image
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}
import services.database.ImageServiceImpl
import services.traits.TokenRepresentation

import scala.language.higherKinds

@ImplementedBy(classOf[ImageServiceImpl])
trait ImageService[T[_]] {

   def create(eventId: Long, token: TokenRepresentation, picture: MultipartFormData.FilePart[Files.TemporaryFile], host: String): T[Image]

   def delete(id: Long)(implicit request: Request[_]): T[Int]

   def findById(id: Long)(implicit request: Request[_]): T[Image]

   def findByEventId(eventId: Long)(implicit request: Request[_]): T[Seq[Image]]

   def findByUserId(userId: Long)(implicit request: Request[_]): T[Seq[Image]]

}
