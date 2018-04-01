package db.services

import db.services.interfaces.EventNewsService
import javax.inject.Inject
import models.{EventNewsDAO, EventUserDAO}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class EventNewsServiceImpl @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with EventNewsService {
   
   val eventNewsTable = TableQuery[EventNewsDAO]
   val userEventTable = TableQuery[EventUserDAO]
   
   override def get(userId: Long, lastReadId: Long) = {
      
      db.run(eventNewsTable.filter {
         _.eventId in userEventTable.filter {
            _.userId === userId
         }.map(_.eventId)
      }.filter {
         _.id > lastReadId
      }.take(20).sortBy(_.id.asc).result)
   }
   
}