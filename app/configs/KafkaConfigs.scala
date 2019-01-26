package configs

import java.util.Properties

import com.google.inject.Inject
import implicits.implicits._
import javax.inject.Singleton
import play.api.Configuration

@Singleton
class KafkaConfigs @Inject()(configuration: Configuration) {

   lazy val props = configuration.get[Properties]("kafka")

}
