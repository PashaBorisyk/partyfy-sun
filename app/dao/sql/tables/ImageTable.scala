package dao.sql.tables

import models.persistient.Image
import slick.jdbc.PostgresProfile.api._

private[dao] class ImageTable(tag: Tag)
   extends Table[Image](tag, "image") {

   def id = column[Long]("id", O.Unique, O.PrimaryKey, O.AutoInc)

   def exist = column[Boolean]("exist")

   def width = column[Long]("width")

   def ratio = column[Double]("ratio")

   def height = column[Long]("height")

   def urlMini = column[String]("url_mini")

   def urlSmall = column[String]("url_small")

   def urlMedium = column[String]("url_medium")

   def urlLarge = column[String]("url_large")

   def urlHuge = column[String]("url_huge")

   def behaviorId = column[Long]("behavior_id")

   def eventId = column[Long]("event_id")

   def creationMills = column[Long]("creation_mills")

   def * = (
      id, exist,
      width, ratio, height,
      urlMini, urlSmall,
      urlMedium, urlLarge,
      urlHuge, behaviorId,
      eventId,
      creationMills
   ) <> (Image.tupled, Image.unapply)

}
