package dao.traits

import com.google.inject.ImplementedBy
import dao.ImageDAOImpl
import models.persistient.Image

import scala.language.higherKinds

@ImplementedBy(classOf[ImageDAOImpl])
trait ImageDAO[T[_]] {

   def create(eventId: Long, image: Image): T[Image]

   def delete(id: Long): T[Int]

   def findById(id: Long): T[Image]

   def findByEventId(eventId: Long): T[Seq[Image]]

   def findByUserId(userId: Long): T[Seq[Image]]

}
