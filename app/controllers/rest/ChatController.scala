package controllers.rest

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import db.services.{ChatMessageService, EventService}
import models.ChatMessageNOSQL
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.collection.{mutable, _}
import scala.concurrent.ExecutionContext
import util._
import implicits.implicits._


class ChatController @Inject()(cc: ControllerComponents, val chatMessageService: ChatMessageService, val eventService: EventService)
                              (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {
   
   lazy val conversations = mutable.LinkedHashMap[Long,mutable.LinkedHashSet[ActorRef]]()
   
   def socket: WebSocket = WebSocket.accept[String, String] { req =>
      logger.info(s"ChatContoroller.scoket with request : $req")
      
      ActorFlow.actorRef { out =>
         val username = req.getQueryString("user_id").getOrElse(return null)
         Props(new WebSocketActor(out,username.toLong))
      }
   }
   
   class WebSocketActor(val out:ActorRef,val userId:Long) extends Actor {
      
      override def preStart(): Unit = {
         eventService.getEventIdsByMemberId(userId).map{ ids =>
            ids.foreach{ eventId=>
               if(!conversations.contains(eventId)){
                  logger.info(s"No conversation with id $eventId found; Creating new one...")
                  conversations(eventId) = mutable.LinkedHashSet()
               }
               conversations(eventId)+= out
            }
         }
         logger.info(s"Now users online: ${conversations.size}")
      }
      
      override def receive = {
         case msg: String =>
            println(msg)
            val chatMessage:ChatMessageNOSQL = msg
            logger.info(chatMessage.toString)
            conversations(chatMessage.eventID).foreach(_!msg)
         
      }
      
      override def postStop(): Unit = {
         eventService.getEventIdsByMemberId(userId).map{ ids =>
            ids.foreach{ eventId=>
               conversations(eventId)-= out
            }
         }
         logger.info(s"Now users online: ${conversations.size}")
      }
      
   }
   
}
