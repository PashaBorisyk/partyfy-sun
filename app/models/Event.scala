package models

import java.io.Serializable

import com.google.gson.annotations.Expose
import slick.jdbc.PostgresProfile.api._

import scala.annotation.meta.{beanGetter, field}
import scala.beans.BeanProperty


case class Event(
                   id:Long = 0L,
                   creatorId:Long = 0L,
                   dateMills:Long = 0L,
                   creationDateMills:Long = 0L,
                   maxMembers:Long = 0L,
                   @(Expose @field)
                   longitude:Double = 0.0,
                   @(Expose @field)
                   latitude:Double = 0.0,
                   @(Expose @field)
                   creatorNickname:String = "",
                   country: String = "",
                   city: String = "",
                   street: String = "",
                   localName: String = "",
                   @(Expose @field)
                   description: String = "",
                   isPublic:Boolean = false,
                   isForOneGender:Boolean = false,
                   isForMale:Boolean = false,
                   eventImageId: Long = 0L,
                   @(Expose @field)
                   creatorsImageUrl: String = "",

) extends Serializable

class EventDAO(tag: Tag) extends Table[Event](tag,"event"){

   def id = column[Long]("id",O.PrimaryKey,O.Unique,O.AutoInc)
   def creatorId = column[Long]("creator_id")
   def dateMills = column[Long]("date_mills")
   def creationDateMills = column[Long]("creation_date_mills")
   def maxMembers = column[Long]("max_members")
   def longtitude = column[Double]("longtitude")
   def latitude = column[Double]("latitude")
   def creatorNickname = column[String]("creator_nickname")
   def country = column[String]("country")
   def city = column[String]("city")
   def street = column[String]("street")
   def localName = column[String]("local_name")
   def description = column[String]("description")
   def isPublic = column[Boolean]("is_public")
   def isForOneGender = column[Boolean]("is_for_one_gender")
   def isForMale = column[Boolean]("is_for_male")
   def eventImageId = column[Long]("event_image_id")
   def creatorsImageUrl = column[String]("creators_image_url")
   
   def * = (
      id,creatorId,
      dateMills,creationDateMills,
      maxMembers,longtitude,
      latitude,creatorNickname,country,
      city,street,
      localName,
      description,isPublic,
      isForOneGender,isForMale,
      eventImageId,creatorsImageUrl
   ) <> (Event.tupled,Event.unapply)
   

}

case class EventUser(
   
                              eventId:Long = 0L,
                              userId: Long = 0L,
                              isNewForMember:Boolean = true
   
) extends Serializable

class EventUserDAO(tag: Tag) extends Table[EventUser](tag,"event_user"){
   
   def eventId = column[Long]("event_id")
   def userId = column[Long]("user_id")
   def isNewForMember = column[Boolean]("is_new_for_member")
   
   def * = (eventId,userId,isNewForMember) <> (EventUser.tupled,EventUser.unapply)
   
}

case class EventHipeImage(
   
   eventId:Long = 0L,
   hipeImageId:Long = 0L
   
)

class EventHipeImageDAO(tag:Tag) extends Table[EventHipeImage](tag,"event_hipe_image"){
   
   def eventId = column[Long]("event_id")
   def hipeImageId = column[Long]("hipe_image_id")
   
   def * = (eventId,hipeImageId) <> (EventHipeImage.tupled,EventHipeImage.unapply)
   
}