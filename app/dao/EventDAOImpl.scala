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

   override def getEventById(id: Long): Future[Seq[(Event, Option[Image])]] = {
      db.run(EventSql.getByIdJoinImages(id))
   }

   override def deleteById(id: Long) = {
      db.run(EventSql.deleteById(id))
   }

   override def create(event: Event, usersIds: Array[Int]) = {
      val insertAction = EventSql
         .insertEvent(event)
         .flatMap { eventId =>
            val eventToUserRecords = usersIds.map { userId =>
               EventToUser(eventId, userId)
            }
            EventSql.addUsersToEvent(eventToUserRecords).map { _ =>
               eventId
            }
         }
      db.run(insertAction)
   }

   override def update(event: Event) = {
      db.run(EventSql.update(event))
   }

   override def getEventsByOwner(userId: Int) = {
      db.run(EventSql.getByOwnerIdJoinImage(userId))
   }

   override def getEventsByMemberId(userId: Int) = {
      db.run(EventSql.getByMemberIdJoinImage(userId))
   }

   override def getEventIdsByMemberId(userId: Int): Future[Seq[Long]] = {
      db.run(EventSql.getIdsByMemberId(userId))
   }

   override def getEvents(userId: Int,
                          latitude: Double,
                          longtitude: Double,
                          lastReadEventId: Long) = {
      db.run(
         EventSql.getPublicEvents(userId, latitude, longtitude, lastReadEventId))
   }

   override def addUserToEvent(eventId: Long, userId: Int) = {
      db.run(EventSql.addUserToEvent(eventId, userId))
   }

   override def cancelEvent(eventId: Long, userId: Int) = {
      db.run(EventSql.cancelEvent(eventId, userId))
   }

   override def removeUserFromEvent(eventId: Long, userId: Int) = {
      db.run(EventSql.deleteUserFromEvent(userId, eventId))
   }

   override def test(): Unit = {}

}
