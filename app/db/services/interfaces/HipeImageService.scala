package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.HipeImageServiceImpl
import models.HipeImage
import play.api.mvc.Request

@ImplementedBy(classOf[HipeImageServiceImpl])
trait HipeImageService[T[_]] {

   def create(eventId: Long, hipeImage: HipeImage)(implicit request: Request[_]): T[Long]

   def delete(id: Long)(implicit request: Request[_]): T[Int]

   def findById(id: Long)(implicit request: Request[_]): T[HipeImage]

   def findByEventId(eventId: Long)(implicit request: Request[_]): T[Seq[HipeImage]]

   def findByUserId(userId: Long)(implicit request: Request[_]): T[Seq[HipeImage]]

}
