package dao

import dao.sql.EventSql
import dao.traits.EventDAO
import javax.inject.Inject
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class EventDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with EventDAO[Future] {

   override def getEventById(id: Long): Future[Seq[(Event, Option[Image])]] = {
      db.run(EventSql.getById(id))
   }

   override def deleteById(id: Long) = {
      db.run(EventSql.deleteById(id))
   }

   override def create(event: (Event, Set[Long])) = {
      db.run(EventSql.create(event).map(_.asInstanceOf[Int]))
   }

   override def update(event: Event) = {
      db.run(EventSql.update(event))
   }

   override def getEventsByOwner(userId: Long) = {
      db.run(EventSql.getByOwnerId(userId))
   }

   override def getEventsByMemberId(userId: Long) = {
      db.run(EventSql.getByMemberId(userId))
   }

   override def getEventIdsByMemberId(userId: Long): Future[Seq[Long]] = {
      db.run(EventSql.getIdsByMemberId(userId))
   }

   override def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long) = {
      db.run(EventSql.getPublicEvents(userId, latitude, longtitude, lastReadEventId))
   }

   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long) = {
      db.run(EventSql.addUserToEvent(eventId,userId))
   }

   override def cancelEvent(userId: Long, eventId: Long) = {
      db.run(EventSql.cancel(eventId,userId))
   }

   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long) = {
      db.run(EventSql.deleteUserFromEvent(userId,eventId))
   }

   override def test(): Unit = {
   }

}