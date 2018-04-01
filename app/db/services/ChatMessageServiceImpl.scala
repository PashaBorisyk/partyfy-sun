package db.services

import db.services.interfaces.ChatMessageService
import implicits.implicits._
import javax.inject.Inject
import models.ChatMessageNOSQL
import services.MongoDBExecutor
import util._

import scala.concurrent.{ExecutionContext, Future}

class ChatMessageServiceImpl @Inject()(
                                     private val db: MongoDBExecutor
                                  )(implicit ec: ExecutionContext) extends ChatMessageService {
   
   lazy val chatCollection = {
      logger.info("Connecting to chat_message_collection")
      val r = db.forName("chat_message_collection")
      logger.info(s"collcetion : $r")
      r
   }
   
   override def create(chatMessage:ChatMessageNOSQL) = {
      logger.info(s"ChatMessagesService.create($chatMessage)")
      Future{
         chatCollection.insert(chatMessage.toDBObject())
      }
   }
   
   override def getByEventId(eventId:Long) = {
      logger.info(s"ChatMessagesService.getByEventId($eventId)")
      Future{
         chatCollection.find(ChatMessageNOSQL().toDBObject()).map{ s =>
            println(s)
            s
         }
      }
   }
   
}