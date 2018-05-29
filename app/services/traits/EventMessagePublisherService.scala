package services.traits

import com.google.inject.ImplementedBy
import services.EventMessagePublisherServiceImpl

@ImplementedBy(classOf[EventMessagePublisherServiceImpl])
trait EventMessagePublisherService {

   def publishCreated[T <: Any](instanceOf:Int,
                                body:T)

   def publishDeleted[T <: Any](instanceOf:Int,
                                body:T)

   def publishUpdated[T <: Any](instanceOf:Int,
                                body:T)


}
