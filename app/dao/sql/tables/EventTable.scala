package dao.sql.tables

import models.persistient.{Event, EventPrivacyType, UserSex}
import implicits._
import slick.jdbc.PostgresProfile.api._

private[sql] class EventTable(tag: Tag) extends Table[Event](tag, "event") {

   def id = column[Long]("id", O.PrimaryKey, O.Unique, O.AutoInc)

   def ownerId = column[Long]("owner_id")

   def dateMills = column[Long]("date_mills")

   def creationDateMills = column[Long]("creation_date_mills")

   def maxMembers = column[Long]("max_members")

   def longtitude = column[Double]("longtitude")

   def latitude = column[Double]("latitude")

   def ownerUsername = column[String]("owner_nickname")

   def country = column[String]("country")

   def city = column[String]("city")

   def street = column[String]("street")

   def localName = column[String]("local_name")

   def description = column[String]("description")

   def openedFor = column[UserSex]("opened_for")

   def privacyType = column[EventPrivacyType]("privacy_type")

   def eventImageId = column[Long]("event_image_id")

   def ownerImageUrl = column[String]("owner_image_url")

   def * = (
      id, ownerId, dateMills,
      creationDateMills, maxMembers, longtitude,
      latitude, ownerUsername, country,
      city, street, localName,
      description, openedFor, privacyType,
      eventImageId, ownerImageUrl
   ) <> (Event.tupled, Event.unapply)

}