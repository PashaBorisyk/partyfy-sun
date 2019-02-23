package services

import javax.inject.Inject
import pdi.jwt.JwtSession
import play.api.Configuration
import services.traits.{JWTCoder, TokenRepresentation}

class JWTCoderImpl @Inject()()(implicit val configuration: Configuration) extends JWTCoder {

   override def encodePrivate(userId: Long, username: String, secret: String): String = {
      val session = JwtSession() ++ (
         ("id", userId),
         ("username", username),
         ("secret", secret),
      )
      if (session.isEmpty()) {
         //            .withClaim(JwtClaim(expiration = Some(System.currentTimeMillis()+ 1000000L)))
         throw new RuntimeException("JWT session can not be empty")
      }

      session.serialize

   }

   override def decodePrivate(jwt: String) = {

      val result = JwtSession.deserialize(jwt)

      val id = result.getAs[Long]("id") match {
         case Some(value) =>
            value
         case None => throw new RuntimeException("Can not get id from private token")
      }
      val username = result.getAs[String]("username") match {
         case Some(value) =>
            value
         case None => throw new RuntimeException("Can not get username from private token")
      }
      val password: String = result.getAs[String]("secret") match {
         case Some(value) =>
            value
         case None => throw new RuntimeException("Can not get password from private token")
      }

      TokenRepresentation(id, username, password)

   }


   override def encodePublic(creds: (String, String)): String = {

      val session = JwtSession() ++ (
         ("username", creds._1),
         ("secret", creds._2)
      )

      if (session.isEmpty()) {
         throw new RuntimeException("JWT session can not be empty")
      }

      session.serialize

   }

   override def decodePublic(jwt: String) = {
      val result = JwtSession.deserialize(jwt)

      (result.get("username") match {
         case Some(username) =>
            Some("username" -> username.toString())
         case None => throw new RuntimeException("Can not get username from public token")
      }, result.get("password") match {
         case Some(password) =>
            Some("password" -> password.toString())
         case None => throw new RuntimeException("Can not get password from public token")
      })

   }
}
