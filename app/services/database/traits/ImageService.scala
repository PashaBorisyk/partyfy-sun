package services.database.traits

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.{Image, UserToImage}
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}
import services.database.ImageServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[ImageServiceImpl])
trait ImageService[T[_]] {

   def create(eventId: Long,picture: MultipartFormData.FilePart[Files.TemporaryFile],
              host: String)(implicit token: TokenRepPrivate): T[Image]

   def delete(id: Long)(implicit token: TokenRepPrivate): T[Int]

   def findById(id: Long)(implicit token: TokenRepPrivate): T[Option[Image]]

   def findByEventId(eventId: Long)(implicit token: TokenRepPrivate): T[Seq[Image]]

   def findByUserId(userId: Long)(implicit token: TokenRepPrivate): T[Seq[Image]]

}
