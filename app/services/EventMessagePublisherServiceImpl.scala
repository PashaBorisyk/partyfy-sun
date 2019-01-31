package services

import controllers.publishers.traits.EventPublisher
import enums.EventMessageActionType
import javax.inject.{Inject, Singleton}
import models.EventMessage
import services.traits.EventMessagePublisherService

@Singleton
class EventMessagePublisherServiceImpl @Inject()(
                                                   val subscriber: EventPublisher
                                                ) extends EventMessagePublisherService {

   override def !+[T <: Any](body: T): Unit = {

      subscriber ! EventMessage(
         eventMessageActionType = EventMessageActionType.CREATED.toString,
         body = body,
      )

   }

   override def !-[T <: Any](body: T): Unit = {
      subscriber ! EventMessage(
         eventMessageActionType = EventMessageActionType.DELETED.toString,
         body = body,
      )
   }

   override def ![T <: Any](body: T): Unit = {
      subscriber ! EventMessage(
         eventMessageActionType = EventMessageActionType.UPDATED.toString,
         body = body,
      )
   }
}
