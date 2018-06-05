package controllers.websocket

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import implicits.implicits._
import javax.inject.{Inject, _}
import models.EventMessage
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import util.{Const, logger}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

@Singleton
class EventPublisherWebSocketConnector @Inject()(cc: ControllerComponents)
                                                (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {
   
   private type ConnectionType = (Int, Int)
   
   private final lazy val connections = mutable.LinkedHashMap[
      ConnectionType, mutable.LinkedHashMap[String, mutable.LinkedHashSet[ConnectionHandler]]
      ]()
   private final val webSocketActor_ = system.actorOf(Props(new MessageListener))
   
   def webSocketActor: ActorRef = webSocketActor_
   
   def socket: WebSocket = WebSocket.accept[String, String] { req =>
      ActorFlow.actorRef { out: ActorRef =>
         logger.debug(s"Incoming websocket connection with request: $req")
         
         val `type` = req.getQueryString(Const.CONNECTION_FOR_TYPE).getOrElse(-1)
         val category = req.getQueryString(Const.CONNECTION_FOR_CATEGORY).getOrElse(-1)
         
         val connectionType = new ConnectionType(`type`, category)
         
         if (connectionType._1 < 0 && connectionType._2 < 0)
            return null
         
         Props(new ConnectionHandler(connectionType, req.remoteAddress, out))
      }
   }
   
   private class ConnectionHandler(connectionType: ConnectionType, remoteAddress: String, out: ActorRef) extends Actor {
      
      override def preStart(): Unit = {
         logger.debug(s"Catching connection $remoteAddress")
         connections(connectionType)(remoteAddress) += this
      }
      
      override def receive: PartialFunction[Any, Unit] = {
         case msg: EventMessage[_] =>
            out ! msg
      }
      
      override def postStop(): Unit = {
         logger.debug(s"Releasing connection $remoteAddress")
         connections(connectionType)(remoteAddress) -= this
      }
      
   }
   
   private class MessageListener extends Actor {
      
      override def preStart(): Unit = {
         logger.debug(s"Starting MessageListener actor")
      }
      
      override def receive: PartialFunction[Any, Unit] = {
         case msg: EventMessage[_] =>
            val key = (msg.`type`, msg.category)
            /*Do some stuff in here */
            connections.get(key).foreach(
               connectionList =>
                  connectionList.foreach(
                     _._2.foreach(_.self ! msg
                     )
                  )
            )
         
      }
      
      override def postStop(): Unit = {
         logger.info(s"Stopping MessageListener actor ")
      }
      
   }
   
}
