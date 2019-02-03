package services.database.traits

import com.google.inject.ImplementedBy
import models.EventNewsDAO
import play.api.mvc.Request
import services.database.EventNewsServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[EventNewsServiceImpl])
trait EventNewsService[T[_]] {

   def get(userId: Long, lastReadId: Long)(implicit request: Request[_]): T[Seq[EventNewsDAO#TableElementType]]

}
