package services.traits

import com.google.inject.ImplementedBy
import models.{TokenRep, TokenRepPrivate, TokenRepRegistration}
import services.JWTCoderImpl


@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder  {

   def encode(tokenRep: TokenRep) : String
   def decodePrivateToken(token:String) : TokenRepPrivate
   def decodeRegistrationToken(token:String) : TokenRepRegistration

   def fromLazyUserId(tokenRep:TokenRep)(userId:Int) = encode(TokenRep(tokenRep)(userId))

}