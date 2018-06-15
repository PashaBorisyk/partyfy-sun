package services.traits

import com.google.inject.ImplementedBy
import services.JWTCoderImpl

@ImplementedBy(classOf[JWTCoderImpl])
trait JWTCoder  {
   def encode(username:String,password:String) : String
   def decode(jwt:String) : String
}
