package services.traits

import com.google.inject.ImplementedBy
import services.JWTCoderImpl

@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder  {
   def encode(creds:(String,String,Long)) : String
   def decode(jwt:String) : (Option[(String,String)],Option[(String,String)],Option[(String,Long)])
}
