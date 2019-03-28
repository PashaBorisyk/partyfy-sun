package dao.sql.tables

import models.persistient.Image
import slick.jdbc.PostgresProfile.api._

private[dao] class ImageTable(tag: Tag)
   extends Table[Image](tag, "image") {

   def id = column[Long]("id", O.Unique, O.PrimaryKey, O.AutoInc)

   def width = column[Long]("width")

   def ratio = column[Double]("ratio")

   def height = column[Long]("height")

   def urlMini = column[String]("url_mini")

   def urlSmall = column[String]("url_small")

   def urlMedium = column[String]("url_medium")

   def urlLarge = column[String]("url_large")

   def urlHuge = column[String]("url_huge")

   def ownerId = column[Int]("owner_id")

   def eventId = column[Long]("event_id")

   def creationMills = column[Long]("creation_mills")

   def * = (
      id,
      width, ratio, height,
      urlMini, urlSmall,
      urlMedium, urlLarge,
      urlHuge, ownerId,
      eventId,
      creationMills
   ) <> (Image.tupled, Image.unapply)

}
