package configs

import java.util.AbstractMap.SimpleImmutableEntry
import java.util.Properties

import com.typesafe.config.{Config, ConfigValue}
import play.api.ConfigLoader

package object implicits {

   implicit val config2Properties = new ConfigLoader[Properties] {
      override def load(config: Config, path: String): Properties = {

         val properties = new Properties()

         config.getConfig(path).entrySet().toArray(Array[SimpleImmutableEntry[String, ConfigValue]]()).foreach { item =>
            properties.put(item.getKey, item.getValue.unwrapped())
         }

         properties
      }
   }

   implicit class PropertiesImplicits(properties:Properties){

      def toMap = {
         var map = Map[String,String]()
         properties.forEach{ (key,value) =>
            map += (key.toString->value.toString)
         }
         map
      }

   }

}
