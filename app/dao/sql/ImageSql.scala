package dao.sql

import dao.sql.tables.{EventTable, ImageTable, UserToImageTable}
import models.persistient._
import slick.jdbc.PostgresProfile.api._

private[dao] object ImageSql {

   private lazy val imageTable = TableQuery[ImageTable]
   private lazy val userToImageTable = TableQuery[UserToImageTable]
   private lazy val eventTable = TableQuery[EventTable]

   def insertImage(image: Image) = {
      (imageTable returning imageTable.map(_.id))
         .into((image, id) => image.copy(id = id)) += image
   }

   def delete(imageId: Long) = {
      _getById(imageId).delete
   }

   def getById(imageId: Long) = {
      _getById(imageId).result.headOption
   }

   private val _getById = Compiled { imageId: Rep[Long] =>
      imageTable
         .filter { image =>
            image.id === imageId
         }
   }

   def findByEventId(eventId: Long) = {
      _findByEventId(eventId).result
   }

   private val _findByEventId = Compiled { eventId: Rep[Long] =>
      imageTable
         .filter { image =>
            image.eventId === eventId
         }
         .sortBy(_.creationMills.desc)
   }

   def findByUserId(userId: Int) = {
      _findByUserId(userId).result
   }

   private val _findByUserId = Compiled { userId: Rep[Int] =>
      imageTable
         .filter { image =>
            image.id in userToImageTable
               .filter(_.userId === userId)
               .map(_.imageId)
         }
         .sortBy(_.creationMills.desc)
   }

   def attachUserToImage(usersToImages: Array[UserToImage]) = {
      userToImageTable ++= usersToImages
   }

}
