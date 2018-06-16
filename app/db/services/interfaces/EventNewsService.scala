package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.EventNewsServiceImpl
import models.EventNewsDAO
import play.api.mvc.Request

import scala.concurrent.Future

@ImplementedBy(classOf[EventNewsServiceImpl])
trait EventNewsService {

  def get(userId:Long,lastReadId:Long)(implicit request: Request[_]) : Future[Seq[EventNewsDAO#TableElementType]]

}
