package models.persistient

import java.io.Serializable

/**
  * Created by pasha on 19.08.2016.
  *
  * used to store all messages for offline users
  *
  */
case class OfflineStore(
                          ownerId: Long = 0L,
                          chatMessages: Long = -0L
                       ) extends Serializable
