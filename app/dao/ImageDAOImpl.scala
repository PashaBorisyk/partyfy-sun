package dao

import com.google.inject.Inject
import dao.sql.{EventSql, ImageSql, Sql}
import dao.traits.ImageDAO
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ImageDAOImpl @Inject()(
                               protected val dbConfigProvider: DatabaseConfigProvider)(
                               implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile]
      with ImageDAO[Future] {

   override def create(image: Image) = {
      val createAction = EventSql
         .getById(image.eventId)
         .zip(ImageSql.insertImage(image))
         .flatMap {
            case (Some(event), image) =>
               EventSql.update(event.copy(eventImageId = image.id)).map(_ => image)
            case (None, image) => Sql(image)
         }
      db.run(createAction)
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

   override def attachUserToImage(usersToImages: Array[UserToImage]) = {
      db.run(ImageSql.attachUserToImage(usersToImages))
   }

}
