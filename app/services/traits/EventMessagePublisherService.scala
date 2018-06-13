package services.traits

import com.google.inject.ImplementedBy
import services.EventMessagePublisherServiceImpl

@ImplementedBy(classOf[EventMessagePublisherServiceImpl])
trait EventMessagePublisherService {

   def !+[T <: Any](body:T)

   def !-[T <: Any](body:T)

   def ![T <: Any](body:T)


}
