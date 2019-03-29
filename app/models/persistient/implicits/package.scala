package models.persistient

import models.{TokenRep, TokenRepPrivate, TokenRepRegistration}
import play.api.libs.json._

import scala.language.higherKinds

package object implicits {

   implicit val myEnumFormat: Format[UserState] = new Format[UserState] {
      override def reads(json: JsValue) =
         JsSuccess(UserState.valueOf(json.as[String]))

      override def writes(o: UserState) = JsString(o.toString)
   }
   implicit val userSexFormat: Format[UserSex] = new Format[UserSex] {
      override def reads(json: JsValue) =
         JsSuccess(UserSex.valueOf(json.as[String]))

      override def writes(o: UserSex) = JsString(o.toString)
   }
   implicit val eventPrivacyTypeFormat: Format[EventPrivacyType] =
      new Format[EventPrivacyType] {
         override def reads(json: JsValue) =
            JsSuccess(EventPrivacyType.valueOf(json.as[String]))

         override def writes(o: EventPrivacyType) = JsString(o.toString)
      }
   implicit val eventStateFormat: Format[EventState] = new Format[EventState] {
      override def reads(json: JsValue) =
         JsSuccess(EventState.valueOf(json.as[String]))

      override def writes(o: EventState) = JsString(o.toString)
   }

   implicit val eventFormat: OFormat[Event] = Json.format[Event]
   implicit val userFormat: OFormat[User] = Json.format[User]
   implicit val imageFormat: OFormat[Image] = Json.format[Image]
   implicit val tokenRepFormat: OFormat[TokenRep] = Json.format[TokenRep]
   implicit val tokenRepRegistrationFormat: OFormat[TokenRepRegistration] =
      Json.format[TokenRepRegistration]
   implicit val tokenRepPrivateFormat: OFormat[TokenRepPrivate] =
      Json.format[TokenRepPrivate]

}
