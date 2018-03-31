package controllers.rest

import javax.inject.Inject

import db.services.EventNewsService
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import implicits.implicits._
import util._

import scala.concurrent.ExecutionContext

class EventNewsControllerImpl @Inject()(
                                          protected val eventNewsService: EventNewsService,
                                          protected val dbConfigProvider: DatabaseConfigProvider,
                                          cc: ControllerComponents

                                       )(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
   
   def get(userId: Long, lastReadId: Long) = Action.async {
      req =>
         logger.info(req.toString())
         eventNewsService.get(userId, lastReadId).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
}