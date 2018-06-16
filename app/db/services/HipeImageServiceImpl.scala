package db.services

import com.google.inject.Inject
import db.services.interfaces.HipeImageService
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class HipeImageServiceImpl @Inject()(
                               protected val dbConfigProvider: DatabaseConfigProvider,
                            )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with HipeImageService {
   
   lazy val hipeImageTable = TableQuery[HipeImageDAO]
   lazy val eventHipeImageTable = TableQuery[EventHipeImageDAO]
   lazy val userHipeImageTable = TableQuery[UserHipeImageDAO]
   lazy val eventTable = TableQuery[EventDAO]
   
   override def create(eventId:Long,hipeImage: HipeImage)(implicit request: Request[_]):Future[Long] = Future{
      val query = hipeImageTable returning hipeImageTable.map(_.id)
      val id = Await.result(db.run(query+=hipeImage),5.second)
      val event = Await.result(db.run(eventTable.filter(_.id === eventId).result.head), 5.second)
      db.run(eventTable.filter(_.id===eventId).update(event.copy(eventImageId = id)))
      id
   }
   
   override def delete(id:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter(_.id===id).delete)
   }
   
   override def findById(id:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter(_.id===id).result.head)
   }

   override def findByEventId(eventId:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter{i=> i.id in eventHipeImageTable.filter(_.eventId === eventId).map(_.hipeImageId)}
         .sortBy(_.creationMills.desc).result)
   }
   
   override def findByUserId(userId:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter{
         i=>
            i.id in userHipeImageTable.filter(_.userId===userId).map(_.hipeImageId)
      }.sortBy(_.creationMills.desc).result)
   }
   
}