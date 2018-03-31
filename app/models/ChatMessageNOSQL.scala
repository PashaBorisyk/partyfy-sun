package models

import java.io.Serializable
import util._
import services.MongoDBExecutor

case class ChatMessageNOSQL(
   
                         id: Long = 0L,
                         senderId: Long = 0L,
                         eventID: Long = 0L,
                         users: Array[Int] = Array(),
                         mills: Long = System.currentTimeMillis(),
                         senderNickname:String = "",
                         message: String = "message example",
                         informative: Boolean = false,
                         execute: Long = 1

) extends Serializable
