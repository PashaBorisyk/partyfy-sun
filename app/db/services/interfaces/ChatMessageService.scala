package db.services.interfaces

import com.google.inject.ImplementedBy
import com.mongodb.{DBObject, WriteResult}
import db.services.ChatMessageServiceImpl
import models.ChatMessageNOSQL

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

@ImplementedBy(classOf[ChatMessageServiceImpl])
trait ChatMessageService {

  def create(chatMessageNOSQL: ChatMessageNOSQL):Future[WriteResult]
  def getByEventId(eventId:Long):Future[ArrayBuffer[DBObject]]

}
