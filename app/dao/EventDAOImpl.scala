package dao

import dao.sql.EventSql
import dao.traits.EventDAO
import javax.inject.Inject
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class EventDAOImpl @Inject()(
                               protected val dbConfigProvider: DatabaseConfigProvider)(
                               implicit
                               ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile]
      with EventDAO[Future] {

   override def getEventById(id: Long) = {
      db.run(EventSql.getByIdJoinImages(id))
   }

   override def deleteById(id: Long) = {
      db.run(EventSql.deleteById(id))
   }

   override def create(event: Event, usersIDss: Array[Int]) = {
      val insertAction = EventSql
         .insertEvent(event)
         .flatMap { eventID =>
            val eventToUserRecords = usersIDss.map { userID =>
               EventToUser(eventID, userID)
            }
            EventSql.addUsersToEvent(eventToUserRecords).map { _ =>
               eventID
            }
         }
      db.run(insertAction)
   }

   override def update(event: Event) = {
      db.run(EventSql.update(event))
   }

   override def getEventsByOwner(userID: Int) = {
      db.run(EventSql.getByOwnerIdJoinImage(userID))
   }

   override def getEventsByMemberId(userID: Int) = {
      db.run(EventSql.getByMemberIdJoinImage(userID))
   }

   override def getEventIDsByMemberID(userID: Int): Future[Seq[Long]] = {
      db.run(EventSql.getIdsByMemberId(userID))
   }

   override def getEvents(userID: Int,
                          latitude: Double,
                          longtitude: Double,
                          lastReadeventID: Long) = {
      db.run(
         EventSql.getPublicEvents(userID, latitude, longtitude, lastReadeventID))
   }

   override def addUserToEvent(eventID: Long, userID: Int) = {
      db.run(EventSql.addUserToEvent(eventID, userID))
   }

   override def cancelEvent(eventID: Long, userID: Int) = {
      db.run(EventSql.cancelEvent(eventID, userID))
   }

   override def removeUserFromEvent(eventID: Long, userID: Int) = {
      db.run(EventSql.deleteUserFromEvent(userID, eventID))
   }

   override def test(): Unit = {}

}
