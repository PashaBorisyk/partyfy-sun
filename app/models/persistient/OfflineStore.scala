package models.persistient

import java.io.Serializable

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by pasha on 19.08.2016.
  *
  * used to store all messages for offline users
  *
  */
case class OfflineStore(
   
   behaviorId:Long = 0L,
   chatMessages:Long = -0L

) extends Serializable

case class OfflineStoreChatMessage(
   
   offlineStoreBehaviorId:Long = 0L,
   chatMessagesId:Long = 0L
   
) extends Serializable




class OfflineStoreChatMessagesTable(tag:Tag) extends Table[OfflineStoreChatMessage](tag:Tag,"offline_store_chat_message"){
   
   def offlineStoreBehaviorId = column[Long]("offline_store_behavior_id")
   def chatMessagesId = column[Long]("chat_messages_id")
   
   def * = (offlineStoreBehaviorId,chatMessagesId) <> (OfflineStoreChatMessage.tupled,OfflineStoreChatMessage.unapply)
   
}


class OfflineStoreTable(tag:Tag) extends Table[OfflineStore](tag:Tag,"offline_store"){

   def behaviorId = column[Long]("behavior_id",O.Unique,O.PrimaryKey,O.AutoInc)
   def chatMessagesId = column[Long]("chat_messages")
   
   def * = (behaviorId,chatMessagesId) <> (OfflineStore.tupled,OfflineStore.unapply)
   
}