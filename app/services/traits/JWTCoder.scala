package services.traits

import com.google.inject.ImplementedBy
import services.JWTCoderImpl

@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder  {
   def encodePrivate(creds:(String,String,Long)) : String
   def decodePrivate(jwt:String) : (Option[(String,String)],Option[(String,String)],Option[(String,Long)])
   def encodePublic(creds:(String,String)) : String
   def decodePublic(jwt:String) : (Option[(String,String)],Option[(String,String)])
}
