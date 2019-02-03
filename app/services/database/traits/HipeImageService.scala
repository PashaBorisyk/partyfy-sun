package services.database.traits

import com.google.inject.ImplementedBy
import models.HipeImage
import play.api.mvc.Request
import services.database.HipeImageServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[HipeImageServiceImpl])
trait HipeImageService[T[_]] {

   def create(eventId: Long, hipeImage: HipeImage)(implicit request: Request[_]): T[Long]

   def delete(id: Long)(implicit request: Request[_]): T[Int]

   def findById(id: Long)(implicit request: Request[_]): T[HipeImage]

   def findByEventId(eventId: Long)(implicit request: Request[_]): T[Seq[HipeImage]]

   def findByUserId(userId: Long)(implicit request: Request[_]): T[Seq[HipeImage]]

}
