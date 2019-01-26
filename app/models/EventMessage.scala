package models

import annotations.Topic

@Topic()
case class EventMessage[T <: Any](
                                    time:Long = System.currentTimeMillis(),
                                    `type`:Int,
                                    category:Int,
                                    eventId:Long = 0l,
                                    userId:Long=0l,
                                    body:Any
                                 )
