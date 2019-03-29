package implicits

import scala.language.implicitConversions

package object implicits {

   implicit class StringImplicits(string: String) {

      @inline def notNullOrEmpty = !(string == null || string.trim.isEmpty)

   }

}
