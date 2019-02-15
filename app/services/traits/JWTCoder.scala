package services.traits

import com.google.inject.ImplementedBy
import services.JWTCoderImpl

case class TokenRep(userId:Long,username:String,password:String)

@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder  {
   def encodePrivate(userId:Long = -1,username:String,password:String) : String
   def decodePrivate(jwt:String) : TokenRep
   def encodePublic(creds:(String,String)) : String
   def decodePublic(jwt:String) : (Option[(String,String)],Option[(String,String)])
}