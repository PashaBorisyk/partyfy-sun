package controllers

import implicits.implicits._
import javax.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.database.EventNewsServiceImpl
import slick.jdbc.JdbcProfile
import util._

import scala.concurrent.ExecutionContext

class EventNewsController @Inject()(
                                         protected val eventNewsService: EventNewsServiceImpl,
                                         protected val dbConfigProvider: DatabaseConfigProvider,
                                         cc: ControllerComponents

                                       )(implicit ec: ExecutionContext)
   extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger(this.getClass)

   def get(userId: Long, lastReadId: Long) = Action.async {
      implicit req=>
         logger.debug(req.toString())
         eventNewsService.get(userId, lastReadId).map {
            result => Ok(result.toArray.toJson)
         }.recover {
            case e: Exception => InternalServerError({e.printStackTrace();e.getMessage})
         }
   }
   
}