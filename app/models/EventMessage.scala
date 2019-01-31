package models

case class EventMessage[T <: Any](
                                    time: Long = System.currentTimeMillis(),
                                    eventMessageActionType: String,
                                    eventId: Long = 0l,
                                    userId: Long = 0l,
                                    body: T,
                                    //Only needed for mobile clients
                                    entityType: String = ""
                                 )
