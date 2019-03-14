package models

import play.api.libs.json.Json.{JsValueWrapper => Wrapper}

sealed abstract class TokenRep (val username:String, val secret:String, val emailAddress:String) {
   def toMapOfFields: Seq[(String, Wrapper)] = Vector(
      "username" -> username,
      "secret" -> secret,
      "emailAddress" -> emailAddress
   )
}

object TokenRep {
   def apply(tokenRep:TokenRep)(userId:Long): TokenRep = TokenRepPrivate(userId,tokenRep.username,tokenRep.username,
      tokenRep.emailAddress)
}

case class TokenRepRegistration(
                                  override val username:String,
                                  override val secret:String,
                                  override val emailAddress:String
                               ) extends TokenRep(username, secret,emailAddress)

case class TokenRepPrivate(
                             userId:Long,
                             override val username:String,
                             override val secret:String,
                             override val emailAddress:String
                          ) extends TokenRep (username,secret,emailAddress) {

   override val toMapOfFields : Seq[(String, Wrapper)] = super.toMapOfFields :+ ("userId" -> userId
      .asInstanceOf[Wrapper])

}