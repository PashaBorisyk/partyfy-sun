package models

case class EventMessage[T <: AnyRef](
                                    time:Long = System.currentTimeMillis(),
                                    `type`:Int,
                                    category:Int,
                                    instanceOf:Int,
                                    body:T
                                    )
