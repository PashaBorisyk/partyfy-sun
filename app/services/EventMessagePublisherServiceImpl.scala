package services

import annotations.Topic
import controllers.publishers.traits.Publisher
import enums.EventMessageActionType
import javax.inject.{Inject, Singleton}
import models.EventMessage
import services.traits.EventMessagePublisherService

@Singleton
@Topic(name = "models.Event")
class EventMessagePublisherServiceImpl @Inject()(
                                                   val publisher: Publisher
                                                ) extends EventMessagePublisherService {

   override def !+[T <: Any](body: T): Unit = {

      publisher.publish(this, EventMessage(
         eventMessageActionType = EventMessageActionType.CREATED.toString,
         body = body,
      )
      )
   }

   override def !-[T <: Any](body: T): Unit = {
      publisher.publish(this, EventMessage(
         eventMessageActionType = EventMessageActionType.DELETED.toString,
         body = body
      )
      )
   }

   override def ![T <: Any](body: T): Unit = {
      publisher.publish(this, EventMessage(
         eventMessageActionType = EventMessageActionType.UPDATED.toString,
         body = body,
      )
      )
   }
}
