package services

import implicits.implicits._
import javax.inject.Inject
import pdi.jwt.JwtSession
import play.api.Configuration
import services.traits.JWTCoder

class JWTCoderImpl @Inject()(val configuration: Configuration) extends JWTCoder {
   
   private lazy val key = configuration.get[String]("play.http.secret.key")
   
   override def encodePrivate(creds: (String, String,Long)):String = {
      (JwtSession() ++ (
         ("username", creds._1),
         ("password", creds._2),
         ("id",creds._3)
      )).run { session =>
         if (!session.isEmpty()) {
            return session.serialize
         }
         ""
      }
   }
   
   override def decodePrivate(jwt: String) = JwtSession.deserialize(jwt)
      .run {
         result =>
            ((result.get("username") match {
               case Some(username) =>
                  Some("username" -> username.toString())
               case None => None
            }),(result.get("password") match {
               case Some(username) =>
                  Some("username" -> username.toString())
               case None => None
            }),(result.get("id") match {
               case Some(username) =>
                  Some("id" -> username.toString().toLong)
               case None => None
            }))
      }
   
   override def encodePublic(creds: (String, String)):String = {
      (JwtSession() ++ (
         ("username", creds._1),
         ("password", creds._2)
      )).run { session =>
         if (!session.isEmpty()) {
            return session.serialize
         }
         ""
      }
   }
   
   override def decodePublic(jwt: String) = JwtSession.deserialize(jwt)
      .run {
         result =>
            ((result.get("username") match {
               case Some(username) =>
                  Some("username" -> username.toString())
               case None => None
            }),(result.get("password") match {
               case Some(username) =>
                  Some("username" -> username.toString())
               case None => None
            }))
      }
}
