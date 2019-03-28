package models

import play.api.libs.json.Json
import play.api.libs.json.Json.{JsValueWrapper => Wrapper}

sealed abstract class TokenRep (val username:String, val secret:String, val email:String) {

   def toMapOfFields: Seq[(String, Wrapper)] = Vector(
      TokenRep.USERNAME -> username,
      TokenRep.SECRET -> secret,
      TokenRep.EMAIL -> email
   )
}

object TokenRep {

   final val USER_ID = "user_id"
   final val USERNAME = "username"
   final val SECRET = "secret"
   final val EMAIL = "email"

   def apply(tokenRep:TokenRep)(userId:Int): TokenRep = TokenRepPrivate(
      userId,
      tokenRep.username, tokenRep.secret,
      tokenRep.email
   )
}

case class TokenRepRegistration(
                                  override val username:String,
                                  override val secret:String,
                                  override val email:String
                               ) extends TokenRep(username, secret,email){

}

case class TokenRepPrivate(
                             userId:Int,
                             override val username:String,
                             override val secret:String,
                             override val email:String,
                             token:String = null
                          ) extends TokenRep (username,secret,email) {


   override val toMapOfFields : Seq[(String, Wrapper)] = super.toMapOfFields :+
      (TokenRep.USER_ID -> Json.toJsFieldJsValueWrapper(userId))

}