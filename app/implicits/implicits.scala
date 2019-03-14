package implicits


import java.util.AbstractMap.SimpleImmutableEntry
import java.util.Properties

import com.typesafe.config.{Config, ConfigValue}
import play.api.ConfigLoader

import scala.language.implicitConversions

package object implicits {

   implicit class StringImplicits(string: String) {

      @inline def notNullOrEmpty = !(string == null || string.trim.isEmpty)

   }

   implicit val config2Properties = new ConfigLoader[Properties] {
      override def load(config: Config, path: String): Properties = {

         val properties = new Properties()

         config.getConfig(path).entrySet().toArray(Array[SimpleImmutableEntry[String, ConfigValue]]()).foreach { item =>
            properties.put(item.getKey, item.getValue.unwrapped())
         }

         properties
      }
   }

}