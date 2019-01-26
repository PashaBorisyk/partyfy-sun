package controllers.publishers.traits

import com.google.inject.ImplementedBy
import controllers.publishers.EventPublisherKafka

@ImplementedBy(classOf[EventPublisherKafka])
trait EventPublisher {

   def !(toPublish: Any)

}
