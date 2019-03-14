package models.persistient

import java.io.Serializable

case class OfflineStoreToChatMessage(

                                       offlineStoreOwnerId:Long = 0L,
                                       chatMessagesId:Long = 0L

                                    ) extends Serializable
