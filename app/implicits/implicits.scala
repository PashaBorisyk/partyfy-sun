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



}