package models.persistient

import java.io.Serializable

import slick.jdbc.PostgresProfile.api._

case class HipeImage(
   
   id:Long = 0l,
   exist:Boolean = false,
   width:Long = 0l,
   ratio:Double = 0d,
   height:Long = 0l,
   urlMini: String = "",
   urlSmall:String = "",
   urlMedium:String = "",
   urlLarge:String = "",
   urlHuge:String = "",
   behaviorId:Long = 0l,
   eventId:Long = 0l,
   creationMills:Long = System.currentTimeMillis()

) extends Serializable


class HipeImageDAO(tag:Tag)
   extends Table[HipeImage](tag,"hipe_image"){
   
   def id = column[Long]("id",O.Unique,O.PrimaryKey,O.AutoInc)
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
      id,exist,
      width,ratio,height,
      urlMini,urlSmall,
      urlMedium,urlLarge,
      urlHuge,behaviorId,
      eventId,
      creationMills
   ) <> (HipeImage.tupled,HipeImage.unapply)
   
}