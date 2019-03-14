package services

import javax.inject.Inject
import models.{TokenRep, TokenRepPrivate, TokenRepRegistration}
import pdi.jwt.JwtSession
import play.api.Configuration
import services.traits.JWTCoder

class JWTCoderImpl @Inject()()(implicit val configuration: Configuration) extends JWTCoder {

   override def encode(tokenRep: TokenRep): String = {
      val session = JwtSession() ++ (tokenRep.toMapOfFields:_*)
      session.serialize
   }

   override def decodeRegistrationToken(jwt: String) = {

      val session = JwtSession.deserialize(jwt)

      if(session.isEmpty())
         throw new RuntimeException("Jwt session must not be empty")

      val (username,secret,emailAddress) = getBasicCredentials(session)

      TokenRepRegistration(username,secret,emailAddress)

   }

   override def decodePrivateToken(jwt: String) = {

      val session = JwtSession.deserialize(jwt)

      if(session.isEmpty())
         throw new RuntimeException("Jwt session must not be empty")

      val (username,secret,emailAddress) = getBasicCredentials(session)

      val userId = session.getAs[Long]("id").getOrElse(
         throw new RuntimeException("Can not get userId from private token")
      )

      TokenRepPrivate(userId,username,secret,emailAddress)

   }

   def getBasicCredentials(session: JwtSession) = {

      val username = session.getAs[String]("username").getOrElse(
         throw new RuntimeException("Can not get username from private token")
      )
      val secret: String = session.getAs[String]("secret").getOrElse(
         throw new RuntimeException("Can not get secret from private token")
      )
      val emailAddress: String = session.getAs[String]("emailAddress").getOrElse(
         throw new RuntimeException("Can not get emailAddress from private token")
      )

      (username,secret,emailAddress)

   }

}
