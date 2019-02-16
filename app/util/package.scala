import java.io.File
import java.util.Random

import play.api.http.HeaderNames
import play.api.mvc.Request
import services.traits.JWTCoder


package object util {

   private lazy val random = new Random
   private lazy val patterns: Array[Char] = Array[Char](
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
      'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
      'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z'
   )

   val NAME_LENGTH = 60
   lazy val logger = play.api.Logger("ADMIN")

   private lazy val folder = new File("./public/images")
   private lazy val path: String = folder.getPath
   lazy val PICTURE_PATH = {
      if (!folder.exists())
         folder.mkdirs()
      path
   }

   def getPublicPath(name: String)(implicit request: Request[_]) = {
      s"${request.host}/public/images/$name"
   }

   def generateName(postfix: String): String = {

      val name = new Array[Char](NAME_LENGTH)

      for (i <- 0 until NAME_LENGTH)
         name(i) = patterns(random.nextInt(patterns.length - 1))

      new String(name) + "." + postfix
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

   def getToken(implicit request: Request[_], jwtCoder: JWTCoder) = jwtCoder.decodePrivate(
      request
         .headers
         .get(HeaderNames.AUTHORIZATION).getOrElse {
         throw new RuntimeException("Request does not have an Authorization header")
      }
   )


}