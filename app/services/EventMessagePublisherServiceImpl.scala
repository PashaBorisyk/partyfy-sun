package services

import controllers.websocket.EventPublisherWebSocketConnector
import javax.inject.{Inject, Singleton}
import models.EventMessage
import services.traits.EventMessagePublisherService
import util.Const

@Singleton
class EventMessagePublisherServiceImpl @Inject()(
                                                val frontendWebSocketConnector: EventPublisherWebSocketConnector
                                                ) extends EventMessagePublisherService{

   override def publishCreated[T <: Any](body: T): Unit ={
      frontendWebSocketConnector.webSocketActor ! EventMessage(
         `type` = Const.MSG_TYPE_CREATED,
         body = body,
         category = Const.MSG_CATEGORY_ENTITY
      )
   }

   override def publishDeleted[T <: Any](body: T): Unit = {
      frontendWebSocketConnector.webSocketActor ! EventMessage(
         `type` = Const.MSG_TYPE_DELETED,
         body = body,
         category = Const.MSG_CATEGORY_ENTITY
      )
   }

   override def publishUpdated[T<:Any](body: T): Unit = {
      frontendWebSocketConnector.webSocketActor ! EventMessage(
         `type` = Const.MSG_TYPE_UPDATED,
         body = body,
         category = Const.MSG_CATEGORY_ENTITY
      )
   }
}
