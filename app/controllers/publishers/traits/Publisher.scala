package controllers.publishers.traits

import com.google.inject.ImplementedBy
import controllers.publishers.EventPublisherKafka
import services.traits.EventMessagePublisherService

@ImplementedBy(classOf[EventPublisherKafka])
trait Publisher {

   def publish(publisher: EventMessagePublisherService, toPublish: Any)

}
