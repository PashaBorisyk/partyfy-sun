package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.EventNewsServiceImpl
import models.EventNewsDAO
import play.api.mvc.Request

@ImplementedBy(classOf[EventNewsServiceImpl])
trait EventNewsService[T[_]] {

   def get(userId: Long, lastReadId: Long)(implicit request: Request[_]): T[Seq[EventNewsDAO#TableElementType]]

}
