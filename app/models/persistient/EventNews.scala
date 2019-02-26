package models.persistient

import java.io.Serializable
import slick.jdbc.PostgresProfile.api._

case class EventNews(
   
   id: Long = 0L,
   eventId: Long = 0L,
   creationMills: Long = 0L,
   whoWrote: Long = 0L,
   newEventMember: Long = 0L,
   removedEventMember: Long = 0L,
   description: String = "",
   isAcceptedByAdmin: Boolean = false,
   isReadByAdmin: Boolean = false,
   memberRemoved: Boolean = false,
   doneByAdmin: Boolean = false,
   eventHoleDeleted: Boolean = false

) extends Serializable


class EventNewsTable(tag: Tag) extends Table[EventNews](tag, "event_news") {
   
   def id = column[Long]("id", O.AutoInc, O.PrimaryKey, O.Unique)
   def eventId = column[Long]("event_id")
   def creationMills = column[Long]("creation_mills")
   def whoWrote = column[Long]("who_write")
   def newEventMember = column[Long]("new_event_member")
   def removedEventMember = column[Long]("removed_event_member")
   def description = column[String]("description")
   def isAcceptedByAdmin = column[Boolean]("is_accepted_by_admin")
   def isReadByAdmin = column[Boolean]("is_read_by_admin")
   def memberRemoved = column[Boolean]("member_removed")
   def doneByAdmin = column[Boolean]("done_by_admin")
   def eventHoleDeleted = column[Boolean]("event_hole_deleted")
   def * =
      (
         id,
         eventId,
         creationMills,
         whoWrote,
         newEventMember,
         removedEventMember,
         description,
         isAcceptedByAdmin,
         isReadByAdmin,
         memberRemoved,
         doneByAdmin,
         eventHoleDeleted
      ) <> (EventNews.tupled, EventNews.unapply)
   
}