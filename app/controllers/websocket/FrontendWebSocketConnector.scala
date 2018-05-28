package controllers.websocket

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.google.gson.JsonSyntaxException
import db.services.{ChatMessageServiceImpl, EventServiceImpl}
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import util.logger
import javax.inject._


import scala.collection.mutable
import scala.concurrent.ExecutionContext

@Singleton
class FrontendWebSocketConnector @Inject()(cc: ControllerComponents, val chatMessageService: ChatMessageServiceImpl,
                                            val eventService: EventServiceImpl)
                                          (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {
   
   private lazy val connections = mutable.LinkedHashMap[Long, mutable.LinkedHashSet[ActorRef]]()
   final val webSocketActor = system.actorOf(Props(new WebSocketActor(2)))
   
   def socket: WebSocket = WebSocket.accept[String, String] { req =>
      logger.info(s"ChatContoroller.socket with request : $req")
      ActorFlow.actorRef { out:ActorRef =>
         logger.debug("Username ")
         Props(new WebSocketActor(2))
      }
   }
   
   class WebSocketActor(val id: Long) extends Actor {
      
      override def preStart(): Unit = {
         logger.debug(s"Starting actor with id $id")
      }
      
      override def receive = {
         case msg: Any =>
            logger.info("Message recieved: " + msg)
      }
      
      override def postStop(): Unit = {
         logger.info(s"Stopping actor with id $id")
      }
      
   }
   
}
