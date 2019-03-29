package configs

import java.util.Properties

import com.google.inject.Inject
import configs.implicits._
import javax.inject.Singleton
import play.api.Configuration

@Singleton
class ParsedConfigurations @Inject()(configuration: Configuration) {

   final lazy val kafkaConfigurations = configuration.get[Properties]("kafka")

}
