package services.database

import com.google.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
import services.database.traits.HipeImageService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class HipeImageServiceImpl @Inject()(
                               protected val dbConfigProvider: DatabaseConfigProvider,
                            )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with HipeImageService[Future] {

   lazy val hipeImageTable = TableQuery[HipeImageDAO]
   lazy val eventHipeImageTable = TableQuery[EventHipeImageDAO]
   lazy val userHipeImageTable = TableQuery[UserHipeImageDAO]
   lazy val eventTable = TableQuery[EventDAO]

   override def create(eventId: Long, hipeImage: HipeImage)(implicit request: Request[_]) = {

      val query = hipeImageTable returning hipeImageTable.map(_.id)

      db.run(eventTable.filter(_.id === eventId).result.head).flatMap { result =>
         db.run(query += hipeImage).zip(db.run(eventTable.filter(_.id === eventId).result.head))
      }.map { result =>

         db.run(eventTable.update(result._2.copy(eventImageId = result._1)))
         result._1.toLong
      }

   }

   override def delete(id:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter(_.id===id).delete)
   }

   override def findById(id:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter(_.id===id).result.head)
   }

   override def findByEventId(eventId:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter{i=> i.id in eventHipeImageTable.filter(_.eventId === eventId).map(_.imageId)}
         .sortBy(_.creationMills.desc).result)
   }

   override def findByUserId(userId:Long)(implicit request: Request[_]) = {
      db.run(hipeImageTable.filter{
         i=>
            i.id in userHipeImageTable.filter(_.userId===userId).map(_.imageId)
      }.sortBy(_.creationMills.desc).result)
   }

}