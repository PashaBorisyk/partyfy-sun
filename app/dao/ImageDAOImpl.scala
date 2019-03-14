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

   override def create(eventId: Long, image:Image) = {
      db.run(ImageSql.create(eventId,image))
   }

   override def delete(id: Long) = {
      db.run(ImageSql.delete(id))
   }

   override def findById(id: Long) = {
      db.run(ImageSql.findById(id))
   }

   override def findByEventId(eventId: Long) = {
      db.run(ImageSql.findByEventId(eventId))
   }

   override def findByUserId(userId: Long) = {
      db.run(ImageSql.findByUserId(userId))
   }

}