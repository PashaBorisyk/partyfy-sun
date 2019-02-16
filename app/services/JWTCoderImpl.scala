package services

import implicits.implicits._
import javax.inject.Inject
import pdi.jwt.JwtSession
import play.api.Configuration
import services.traits.{JWTCoder, TokenRep}

class JWTCoderImpl @Inject()(val configuration: Configuration) extends JWTCoder {

   private lazy val key = configuration.get[String]("play.http.secret.key")

   override def encodePrivate(userId:Long, username:String, password:String): String = {
      (JwtSession() ++ (
         ("id", userId),
         ("username", username),
         ("password", password),
      )).run { session =>
         if (!session.isEmpty()) {
            return session.serialize
         }
         throw new RuntimeException("JWT session can not be empty")
      }
   }

   override def decodePrivate(jwt: String) = JwtSession.deserialize(jwt)
      .run {
         result =>

            val username = result.get("username") match {
               case Some(username) =>
                  username.toString()
               case None => throw new RuntimeException("Can not get username from private token")
            }
            val password = result.get("password") match {
               case Some(password) =>
                  password.toString()
               case None => throw new RuntimeException("Can not get password from private token")
            }
            val id = result.get("id") match {
               case Some(id) =>
                  id.toString().toLong
               case None => throw new RuntimeException("Can not get id from private token")
            }

            TokenRep(id, username, password)
      }


   override def encodePublic(creds: (String, String)): String = {
      (JwtSession() ++ (
         ("username", creds._1),
         ("password", creds._2)
      )).run { session =>
         if (!session.isEmpty()) {
            return session.serialize
         }
         throw new RuntimeException("JWT session can not be empty")
      }
   }

   override def decodePublic(jwt: String) = JwtSession.deserialize(jwt)
      .run {
         result =>
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
