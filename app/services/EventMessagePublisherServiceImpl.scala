package services

import controllers.publishers.traits.EventPublisher
import javax.inject.{Inject, Singleton}
import models.EventMessage
import services.traits.EventMessagePublisherService
import util.Const

@Singleton
class EventMessagePublisherServiceImpl @Inject()(
                                                   val subscriber: EventPublisher
                                                ) extends EventMessagePublisherService {

   override def !+[T <: Any](body: T): Unit = {

      subscriber ! EventMessage(
         `type` = Const.MSG_TYPE_CREATED,
         body = body,
         category = Const.MSG_CATEGORY_ENTITY
      )

   }

   override def !-[T <: Any](body: T): Unit = {
      subscriber ! EventMessage(
         `type` = Const.MSG_TYPE_DELETED,
         body = body,
         category = Const.MSG_CATEGORY_ENTITY
      )
   }

   override def ![T <: Any](body: T): Unit = {
      subscriber ! EventMessage(
         `type` = Const.MSG_TYPE_UPDATED,
         body = body,
         category = Const.MSG_CATEGORY_ENTITY
      )
   }
}
