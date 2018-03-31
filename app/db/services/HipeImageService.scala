package db.services

import com.google.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class HipeImageService @Inject()(
                               protected val dbConfigProvider: DatabaseConfigProvider,
                            )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] {
   
   lazy val hipeImageTable = TableQuery[HipeImageDAO]
   lazy val eventHipeImageTable = TableQuery[EventHipeImageDAO]
   lazy val userHipeImageTable = TableQuery[UserHipeImageDAO]
   lazy val eventTable = TableQuery[EventDAO]
   
   def create(eventId:Long,hipeImage: HipeImage) = Future{
      val query = hipeImageTable returning hipeImageTable.map(_.id)
      val id = Await.result(db.run(query+=hipeImage),5.second)
      val event = Await.result(db.run(eventTable.filter(_.id === eventId).result.head), 5.second)
      db.run(eventTable.filter(_.id===eventId).update(event.copy(eventImageId = id)))
      id
   }
   
   def delete(id:Long) = {
      db.run(hipeImageTable.filter(_.id===id).delete)
   }
   
   def get(id:Long) = {
      db.run(hipeImageTable.filter(_.id===id).result)
   }
   
   def getByEventId(eventId:Long) = {
      db.run(hipeImageTable.filter{i=> i.id in eventHipeImageTable.filter(_.eventId === eventId).map(_.hipeImageId)}
         .sortBy(_.creationMills.desc).result)
   }
   
   def getByUserId(userId:Long) = {
      db.run(hipeImageTable.filter{
         i=>
            i.id in userHipeImageTable.filter(_.userId===userId).map(_.hipeImageId)
      }.sortBy(_.creationMills.desc).result)
   }
   
}