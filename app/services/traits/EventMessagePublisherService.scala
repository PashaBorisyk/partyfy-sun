package services.traits

import com.google.inject.ImplementedBy
import services.EventMessagePublisherServiceImpl

@ImplementedBy(classOf[EventMessagePublisherServiceImpl])
trait EventMessagePublisherService {

   def publishCreated[T <: Any](body:T)

   def publishDeleted[T <: Any](body:T)

   def publishUpdated[T <: Any](body:T)


}
