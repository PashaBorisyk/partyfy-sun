package models

case class EventMessage[T <: Any](
                                    time: Long = System.currentTimeMillis(),
                                    eventMessageActionType: String,
                                    eventID: Long = 0l,
                                    //user who initiated this Event fire
                                    creatorId: Long = 0l,
                                    userIDs: Array[Long] = Array(),
                                    body: T,
                                    //Only needed for mobile clients
                                    entityType: String = ""
                                 )
