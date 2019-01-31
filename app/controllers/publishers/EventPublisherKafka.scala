package controllers.publishers


import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.Materializer
import annotations.Topic
import com.google.inject.Inject
import configs.KafkaConfigs
import controllers.publishers.traits.EventPublisher
import implicits.implicits._
import javax.inject.Singleton
import models.EventMessage
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import util.logger

import scala.concurrent.ExecutionContext

@Singleton()
class EventPublisherKafka @Inject()(kafkaConfigs: KafkaConfigs)(implicit system: ActorSystem, mat: Materializer,
                                                                ec: ExecutionContext) extends EventPublisher {

   private final lazy val connections = scala.collection.mutable.TreeMap[String, ConnectionHandler]()

   private final val messageProxyActor = system.actorOf(Props(new MessageProxyActor))

   private final val producer = new KafkaProducer[String, String](kafkaConfigs.props)

   override def !(toPublish: Any): Unit = messageProxyActor ! toPublish

   @inline def getTopicName(any: Any) = {

      val reflectedClass = any.getClass

      val topicAnnotation = reflectedClass.getAnnotation(classOf[Topic])
      if (topicAnnotation == null)
         throw new RuntimeException(s"Sent type must have ${classOf[Topic].getCanonicalName} annotation")

      if (topicAnnotation.name().notNullOrEmpty)
         topicAnnotation.name()
      else
         reflectedClass.getName

   }

   private class ConnectionHandler(topic: String) {

      def send(msg: Any) = {
         logger.debug(s"Sending message to topic : $topic")
         producer.send(new ProducerRecord[String, String](topic, msg.toJson))
      }

   }

   private class MessageProxyActor extends Actor {

      override def preStart(): Unit = {
         logger.debug(s"Starting MessageProxy actor")


      }

      override def receive: PartialFunction[Any, Unit] = {
         case msg: EventMessage[_] =>
            val topic = getTopicName(msg.body)
            val handler = connections.getOrElse(topic, {
               val connectionHandler: ConnectionHandler = new ConnectionHandler(topic)
               connections(topic) = connectionHandler
               connectionHandler
            })

            handler.send(msg)

      }

      override def postStop(): Unit = {
         logger.info(s"Stopping MessageProxyActor actor")
      }

   }

}
