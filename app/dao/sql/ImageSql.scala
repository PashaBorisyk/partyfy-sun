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

   def delete(imageID: Long) = {
      _getById(imageID).delete
   }

   def getById(imageID: Long) = {
      _getById(imageID).result.headOption
   }

   private val _getById = Compiled { imageID: Rep[Long] =>
      imageTable
         .filter { image =>
            image.id === imageID
         }
   }

   def findByeventID(eventID: Long) = {
      _findByeventID(eventID).result
   }

   private val _findByeventID = Compiled { eventID: Rep[Long] =>
      imageTable
         .filter { image =>
            image.eventID === eventID
         }
         .sortBy(_.creationMills.desc)
   }

   def findByuserID(userID: Int) = {
      _findByuserID(userID).result
   }

   private val _findByuserID = Compiled { userID: Rep[Int] =>
      imageTable
         .filter { image =>
            image.id in userToImageTable
               .filter(_.userID === userID)
               .map(_.imageID)
         }
         .sortBy(_.creationMills.desc)
   }

   def attachUserToImage(usersToImages: Array[UserToImage]) = {
      userToImageTable ++= usersToImages
   }

}
