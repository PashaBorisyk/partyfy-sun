package services.traits

import com.google.inject.ImplementedBy
import models.{TokenRep, TokenRepPrivate, TokenRepRegistration}
import services.JWTCoderImpl

@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder {

   def encode(tokenRep: TokenRep): String

   def decodePrivateToken(token: String): TokenRepPrivate

   def decodeRegistrationToken(token: String): TokenRepRegistration

   def fromLazyUserID(tokenRep: TokenRep)(userID: Int) =
      encode(TokenRep(tokenRep)(userID))

}
