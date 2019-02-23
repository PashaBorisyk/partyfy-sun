package services.traits

import com.google.inject.ImplementedBy
import services.JWTCoderImpl

case class TokenRepresentation(userId:Long, username:String, secret:String)

@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder  {
   def encodePrivate(userId:Long = -1,username:String,password:String) : String
   def decodePrivate(jwt:String) : TokenRepresentation
   def encodePublic(creds:(String,String)) : String
   def decodePublic(jwt:String) : (Option[(String,String)],Option[(String,String)])
}