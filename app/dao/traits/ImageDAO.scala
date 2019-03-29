package dao.traits

import com.google.inject.ImplementedBy
import dao.ImageDAOImpl
import models.persistient.{Image, UserToImage}

import scala.language.higherKinds

@ImplementedBy(classOf[ImageDAOImpl])
trait ImageDAO[T[_]] {

   def create(image: Image): T[Image]

   def delete(id: Long): T[Int]

   def getById(id: Long): T[Option[Image]]

   def findByEventId(eventId: Long): T[Seq[Image]]

   def findByUserId(userId: Int): T[Seq[Image]]

   def attachUserToImage(usersToImages: Array[UserToImage]): T[Option[Int]]

}
