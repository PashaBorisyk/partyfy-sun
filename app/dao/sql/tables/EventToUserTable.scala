package dao.sql.tables

import models.persistient.EventToUser
import slick.jdbc.PostgresProfile.api._

private[sql] class EventToUserTable(tag: Tag)
   extends Table[EventToUser](tag, "event_to_user") {

   def eventID = column[Long]("event_id")

   def userID = column[Int]("user_id")

   def isNewForMember = column[Boolean]("is_new_for_member")

   def * =
      (eventID, userID, isNewForMember) <> (EventToUser.tupled, EventToUser.unapply)

}
