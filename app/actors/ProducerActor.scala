package actors

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import configs.ParsedConfigurations
import javax.inject.Inject
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import play.api.Logger

class ProducerActor @Inject()(
                                actorSystem: ActorSystem,
                                parsedConfigurations: ParsedConfigurations)(implicit mat: Materializer)
   extends Actor
      with ActorLogging {

   private final val logger = Logger("application")

   val producerSettings =
      ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
         .withBootstrapServers("localhost:9092")

   override def preStart() = logger.debug("Starting kafka producer actor")

   override def receive = {
      case eventChanges: Protocol with Record =>
         logger.debug(s"Sending record to topic : ${eventChanges.TOPIC_NAME}")
         Source
            .single(eventChanges)
            .map(_.toJson)
            .map(value =>
               new ProducerRecord[String, String](eventChanges.TOPIC_NAME, value))
            .runWith(Producer.plainSink(producerSettings))

      case _ => logger.debug("Unknown message type")
   }

   override def postStop() = logger.debug("Stopped kafka producer actor")

}
