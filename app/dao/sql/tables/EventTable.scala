package dao.sql.tables

import models.persistient.Event
import slick.jdbc.PostgresProfile.api._

private[sql] class EventTable(tag: Tag) extends Table[Event](tag, "event") {

   def id = column[Long]("id", O.PrimaryKey, O.Unique, O.AutoInc)

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
      id, creatorId,
      dateMills, creationDateMills,
      maxMembers, longtitude,
      latitude, creatorNickname, country,
      city, street,
      localName,
      description, isPublic,
      isForOneGender, isForMale,
      eventImageId, creatorsImageUrl
   ) <> (Event.tupled, Event.unapply)

}