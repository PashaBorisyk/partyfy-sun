package controllers.publishers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import controllers.publishers.traits.Publisher
import implicits.implicits._
import javax.inject.{Inject, _}
import models.{Event, EventMessage}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import services.traits.EventMessagePublisherService
import util.logger

import scala.collection.mutable
import scala.concurrent.ExecutionContext

@Singleton
class EventPublisherWebSocket @Inject()(cc: ControllerComponents)
                                       (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
   extends AbstractController(cc) with Publisher {
   
   private type ConnectionType = (Int, Int)
   
   private final lazy val connections = mutable.LinkedHashMap[
      ConnectionType, mutable.LinkedHashMap[String, mutable.LinkedHashSet[ConnectionHandler]]
      ]()
   private final val webSocketActor_ = system.actorOf(Props(new MessageListener))

   override def publish(publisher: EventMessagePublisherService, toPublish: Any): Unit = webSocketActor_ ! toPublish
   
   def socket(forType:Long,forCategory:Long): WebSocket = WebSocket.accept[String, String] { req =>
      ActorFlow.actorRef ({ out: ActorRef =>
         logger.debug(s"Incoming websocket connection with request: $req}")

         val `type` = forType
         val category = forCategory
         
         val connectionType = new ConnectionType(`type`, category)
         
         Props(new ConnectionHandler(connectionType, req.remoteAddress, out))
      })
   }
   
   private class ConnectionHandler(connectionType: ConnectionType, remoteAddress: String, out: ActorRef) extends Actor {
      
      override def preStart(): Unit = {
         logger.debug(s"Creating connection $remoteAddress with type : $connectionType")
         if (!connections.contains(connectionType))
            connections.put(connectionType, mutable.LinkedHashMap())
         
         if (!connections(connectionType).contains(remoteAddress))
            connections(connectionType).put(remoteAddress, mutable.LinkedHashSet())
         connections(connectionType)(remoteAddress) += this
      }
      
      override def receive: PartialFunction[Any, Unit] = {
         case msg: EventMessage[_] =>
            if (connectionType._1 > 0 && connectionType._2 > 0)
               out ! msg
            else {
               msg.body match {
                  case e:Event =>
                     out ! e.toExposedJson
                  case userRegistration:String =>
                     out ! userRegistration.toExposedJson
                  case _ =>
                     out ! "Next time bro"
   
               }
            }
      }
      
      override def postStop(): Unit = {
         logger.debug(s"Releasing connection $remoteAddress with type $connectionType")
         connections(connectionType)(remoteAddress) -= this
      }
      
   }
   
   private class MessageListener extends Actor {
      
      override def preStart(): Unit = {
         logger.debug(s"Starting MessageListener actor")
      }
      
      override def receive: PartialFunction[Any, Unit] = {
         case msg: EventMessage[_] =>
            //(msg.`type`, msg.category)
            val key = (123, 123)
            /*Do some stuff in here */
            connections.get(key).foreach(
               connectionList =>
                  connectionList.foreach(
                     _._2.foreach(_.self ! msg
                     )
                  )
            )
            connections.get((-1, -1)).foreach(
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
