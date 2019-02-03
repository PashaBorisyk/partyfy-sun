package models

case class EventMessage[T <: Any](
                                    time: Long = System.currentTimeMillis(),
                                    eventMessageActionType: String,
                                    eventId: Long = 0l,
                                    //user who initiated this Event fire
                                    creatorId: Long = 0l,
                                    userIds: Array[Long] = Array(),
                                    body: T,
                                    //Only needed for mobile clients
                                    entityType: String = ""
                                 )
