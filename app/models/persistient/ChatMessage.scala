package models.persistient

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




