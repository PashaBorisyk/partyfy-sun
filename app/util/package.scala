import java.util.Random

package object util {

   private lazy val random = new Random
   private lazy val charsUTF8 = Array(
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
      'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
      'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z'
   )

   val NAME_LENGTH = 60

   def generateName(postfix: String): String = {

      val name = (0 until NAME_LENGTH).map { _ =>
         charsUTF8(random.nextInt(charsUTF8.length - 1))
      }
      new String(name.toArray) + "." + postfix
   }

   @inline def isPrimitiveOrString(any: Any) =
      any.isInstanceOf[String] ||
         any.isInstanceOf[Int] ||
         any.isInstanceOf[Byte] ||
         any.isInstanceOf[Short] ||
         any.isInstanceOf[Long] ||
         any.isInstanceOf[Float] ||
         any.isInstanceOf[Double] ||
         any.isInstanceOf[Boolean]

}
