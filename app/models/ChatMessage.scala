package models

import java.io.Serializable
import slick.jdbc.PostgresProfile.api._

case class ChatMessage(
   
   id: Long = 0L,
   senderId: Long = 0L,
   eventID: Long = 0L,
   mills: Long = System.currentTimeMillis(),
   message: String = "message example",
   informative: Boolean = false,
   execute: Long = 1
   
) extends Serializable

case class ChatMessageUser(

   chatMessageId:Long = 0L,
   userId:Long = 0L
                          
)

class ChatMessageUserDAO(tag:Tag) extends Table[ChatMessageUser](tag,"chat_message_user"){
   
   def chatMessageId = column[Long]("chat_message_id")
   def userId = column[Long]("user_id")
   
   def * = (chatMessageId,userId) <> (ChatMessageUser.tupled,ChatMessageUser.unapply)
}

class ChatMessageDAO(tag:Tag) extends Table[ChatMessage](tag,"chat_message"){

   def id = column[Long]("id",O.PrimaryKey)
   def senderId = column[Long]("sender_id")
   def eventId = column[Long]("userId")
   def mills = column[Long]("mills")
   def message = column[String]("message")
   def informative = column[Boolean]("informative")
   def execute = column[Long]("execute")

   def * = (id,senderId,eventId,mills,message,informative,execute) <> (ChatMessage.tupled,ChatMessage.unapply)

}