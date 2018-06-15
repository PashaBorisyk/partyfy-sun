package services

import javax.inject.Inject
import play.api.Configuration
import services.traits.JWTCoder
import io.really.jwt.{Algorithm, JWT}
import play.api.libs.json.Json
import util.logger


class JWTCoderImpl @Inject()( val configuration: Configuration) extends JWTCoder{
   
   private lazy val key = configuration.get[String]("play.http.secret.key")
   
   override def encode(username: String, password: String): String = {
      val payload = Json.obj("username"->username,"password"->password)
      val jwt = JWT.encode("secret-key",payload,algorithm = Some(Algorithm.HS512))
      logger.debug(s"Result token: $jwt")
      jwt
   }
   
   override def decode(jwt: String): String = {
      val decodeResult = JWT.decode(jwt,Some(key))
//      decodeResult match {
//         case JWT(header,payload) =>
//            logger.debug(s"Success! Header : $header ; payload : $payload")
//         case some=>
//            logger.debug(s"$some")
//      }
      return "done"
   }
}
