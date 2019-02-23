package services.database.traits

import com.google.inject.ImplementedBy
import models.persistient.HipeImage
import play.api.libs.Files
import play.api.mvc.{MultipartFormData, Request}
import services.database.HipeImageServiceImpl
import services.traits.TokenRepresentation

import scala.language.higherKinds

@ImplementedBy(classOf[HipeImageServiceImpl])
trait HipeImageService[T[_]] {

   def create(eventId: Long, token: TokenRepresentation, picture: MultipartFormData.FilePart[Files.TemporaryFile], host: String): T[HipeImage]

   def delete(id: Long)(implicit request: Request[_]): T[Int]

   def findById(id: Long)(implicit request: Request[_]): T[HipeImage]

   def findByEventId(eventId: Long)(implicit request: Request[_]): T[Seq[HipeImage]]

   def findByUserId(userId: Long)(implicit request: Request[_]): T[Seq[HipeImage]]

}
