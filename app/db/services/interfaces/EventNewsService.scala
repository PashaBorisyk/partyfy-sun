package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.EventNewsServiceImpl
import models.EventNewsDAO

import scala.concurrent.Future

@ImplementedBy(classOf[EventNewsServiceImpl])
trait EventNewsService {

  def get(userId:Long,lastReadId:Long) : Future[Seq[EventNewsDAO#TableElementType]]

}
