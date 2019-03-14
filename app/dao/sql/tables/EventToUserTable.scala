package dao.sql.tables

import models.persistient.EventToUser
import slick.jdbc.PostgresProfile.api._

private[sql] class EventToUserTable(tag: Tag) extends Table[EventToUser](tag, "event_to_user") {

   def eventId = column[Long]("event_id")

   def userId = column[Long]("user_id")

   def isNewForMember = column[Boolean]("is_new_for_member")

   def * = (eventId, userId, isNewForMember) <> (EventToUser.tupled, EventToUser.unapply)

}
