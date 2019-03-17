package dao.sql

import dao.sql.tables.{EventTable, EventToImageTable, ImageTable, UserToImageTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

private[dao] object ImageSql {

   private lazy val imageTable = TableQuery[ImageTable]
   private lazy val eventToImageTable = TableQuery[EventToImageTable]
   private lazy val userToImageTable = TableQuery[UserToImageTable]
   private lazy val eventTable = TableQuery[EventTable]

   def create(eventId: Long, image: Image)(implicit ec: ExecutionContext) = {
      val query = imageTable returning imageTable.map(_.id)
      eventTable.filter(_.id === eventId).result.head.zip(query += image).flatMap {
         eventWithImageID =>
            eventTable.insertOrUpdate(eventWithImageID._1.copy(eventImageId = eventWithImageID._2)).map { _ =>
               image.copy(id = eventWithImageID._2)
            }
      }
   }

   def delete(id: Long) = {
      imageTable.filter { image => image.id === id }.delete
   }

   def findById(id: Long) = {
      imageTable.filter { image => image.id === id }.result.head
   }

   def findByEventId(eventId: Long) = {
      imageTable.filter { image =>
         image.id in eventToImageTable.filter(_.eventId === eventId).map(_.imageId)
      }.sortBy(_.creationMills.desc).result
   }

   def findByUserId(userId: Long) = {

      imageTable.filter { image =>
         image.id in userToImageTable.filter(_.userId === userId).map(_.imageId)
      }.sortBy(_.creationMills.desc).result

   }

}
