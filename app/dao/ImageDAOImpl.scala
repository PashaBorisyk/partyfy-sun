package dao

import com.google.inject.Inject
import dao.sql.ImageSql
import dao.traits.ImageDAO
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ImageDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with ImageDAO[Future] {

   override def create(image:Image,usersToImages: Array[UserToImage]) = {
      db.run(ImageSql.create(image).zip(ImageSql.attachToUser(usersToImages)))
   }

   override def delete(id: Long) = {
      db.run(ImageSql.delete(id))
   }

   override def getById(id: Long) = {
      db.run(ImageSql.getById(id))
   }

   override def findByEventId(eventId: Long) = {
      db.run(ImageSql.findByEventId(eventId))
   }

   override def findByUserId(userId: Int) = {
      db.run(ImageSql.findByUserId(userId))
   }

   override def attachToUser(usersToImages: Array[UserToImage]): Future[Option[Int]] = {
      db.run(ImageSql.attachToUser(usersToImages))
   }

}