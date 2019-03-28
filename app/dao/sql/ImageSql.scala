package dao.sql

import dao.sql.tables.{EventTable, ImageTable, UserToImageTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

private[dao] object ImageSql {

   private lazy val imageTable = TableQuery[ImageTable]
   private lazy val userToImageTable = TableQuery[UserToImageTable]
   private lazy val eventTable = TableQuery[EventTable]

   def create(image: Image)(implicit ec: ExecutionContext) = {
      val query = imageTable returning imageTable.map(_.id)
      eventTable
         .filter(_.id === image.eventId)
         .result
         .headOption
         .zip(query += image)
         .flatMap {
            case (Some(event), imageId) =>
               eventTable
                  .insertOrUpdate(event.copy(eventImageId = imageId))
                  .map { _ =>
                     image.copy(id = imageId)
                  }
            case (None,imageId)=> Sql(image.copy(id = imageId))
         }
   }

   def delete(id: Long) = {
      imageTable
         .filter { image => image.id === id }
         .delete
   }

   def getById(id: Long) = {
      imageTable
         .filter { image => image.id === id }
         .result
         .headOption
   }

   def findByEventId(eventId: Long) = {
      imageTable
         .filter { image => image.eventId === eventId }
         .sortBy(_.creationMills.desc)
         .result
   }

   def findByUserId(userId: Int) = {

      imageTable
         .filter { image =>
            image.id in userToImageTable
               .filter(_.userId === userId)
               .map(_.imageId)
         }
         .sortBy(_.creationMills.desc)
         .result

   }

   def attachToUser(usersToImages: Array[UserToImage])(implicit ec: ExecutionContext) = {
      userToImageTable ++= usersToImages
   }

}
