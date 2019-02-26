package dao.traits

import com.google.inject.ImplementedBy
import dao.EventNewsDAOImpl
import models.persistient.EventNewsTable
import play.api.mvc.Request

@ImplementedBy(classOf[EventNewsDAOImpl])
trait EventNewsDAO[T[_]] {

   def get(userId: Long, lastReadId: Long)(implicit request: Request[_]): T[Seq[EventNewsTable#TableElementType]]

}
