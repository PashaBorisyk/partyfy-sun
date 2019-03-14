package dao.sql.tables

import slick.jdbc.PostgresProfile.api._
import models.persistient.EventToImage

private[sql] class EventToImageTable(tag: Tag) extends Table[EventToImage](tag, "event_to_image") {

   def eventId = column[Long]("event_id")

   def imageId = column[Long]("image_id")

   def * = (eventId, imageId) <> (EventToImage.tupled, EventToImage.unapply)

}
