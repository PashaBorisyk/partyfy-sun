package controllers.rest

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.google.gson.JsonSyntaxException
import db.services.{ChatMessageServiceImpl, EventServiceImpl}
import implicits.implicits._
import javax.inject.Inject
import models.ChatMessageNOSQL
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import util._

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class ChatController @Inject()(cc: ControllerComponents, val chatMessageService: ChatMessageServiceImpl, val eventService: EventServiceImpl)
                              (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {

  lazy val conversations = mutable.LinkedHashMap[Long, mutable.LinkedHashSet[ActorRef]]()

  lazy val testId = 0

  def socket: WebSocket = WebSocket.accept[String, String] { req =>
    logger.info(s"ChatContoroller.scoket with request : $req")

    ActorFlow.actorRef { out =>
      val username = req.getQueryString("user_id").orElse(Some("0"))
      logger.debug("Username " + username)
      Props(new WebSocketActor(out, username.get.toLong))
    }
  }

  class WebSocketActor(val out: ActorRef, val userId: Long) extends Actor {

    override def preStart(): Unit = {
      var found = false

      eventService.getEventIdsByMemberId(userId).map { ids =>
        ids.foreach { eventId =>
          logger.debug(s"Event with id $eventId found")
          found = true
          if (!conversations.contains(eventId)) {
            logger.info(s"No conversation with id $eventId found; Creating new conversation")
            conversations(eventId) = mutable.LinkedHashSet()
          }
          conversations(eventId) += out
        }
      }

      if (!found) {
        if (!conversations.contains(testId))
          conversations(testId) = mutable.LinkedHashSet()

        logger.debug(s"No events for user with id: $userId found. Running in test mode")
        conversations(testId) += out
      }

      logger.info(s"Now users online: ${conversations.size}")
    }

    override def receive = {
      case msg: String =>
        println(msg)
        var chatMessage: ChatMessageNOSQL = null
        try {
          chatMessage = msg
        } catch {
          case _: JsonSyntaxException =>
            chatMessage = ChatMessageNOSQL(message = msg)
        }
        logger.info(chatMessage.toJson)
        conversations(chatMessage.eventID).foreach(_ ! chatMessage.toJson)

    }

    override def postStop(): Unit = {
      conversations(testId) -= out
      eventService.getEventIdsByMemberId(userId).map { ids =>
        ids.foreach { eventId =>
          conversations(eventId) -= out
        }
      }
      logger.info(s"Now users online: ${conversations.size}")
    }

  }

}
