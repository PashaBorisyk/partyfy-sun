package controllers.rest

import db.services.EventNewsServiceImpl
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import util._

import scala.concurrent.ExecutionContext

class EventNewsControllerImpl @Inject()(
                                         protected val eventNewsService: EventNewsServiceImpl,
                                         protected val dbConfigProvider: DatabaseConfigProvider,
                                         cc: ControllerComponents

                                       )(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
   
   def get(userId: Long, lastReadId: Long) = Action.async {
      req =>
         logger.debug(req.toString())
         eventNewsService.get(userId, lastReadId).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
}