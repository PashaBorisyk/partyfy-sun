package dao

import dao.traits.EventNewsDAO
import javax.inject.Inject
import models.persistient.{EventNewsTable, EventToUserTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class EventNewsDAOImpl @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with EventNewsDAO[Future] {
   
   val eventNewsTable = TableQuery[EventNewsTable]
   val userEventTable = TableQuery[EventToUserTable]
   
   override def get(userId: Long, lastReadId: Long)(implicit request: Request[_]) = {
      db.run(eventNewsTable.filter {
         _.eventId in userEventTable.filter {
            _.userId === userId
         }.map(_.eventId)
      }.filter {
         _.id > lastReadId
      }.take(20).sortBy(_.id.asc).result)
   
   }
   
}