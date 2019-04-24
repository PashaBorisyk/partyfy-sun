package models


import models.dto.SearchableUserForm
import models.persistient.{Event, EventPrivacyType, EventState, Image, User, UserSex, UserState}
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.functional.syntax._

import scala.language.higherKinds

package object implicits {

   implicit val config: Aux[Json.MacroOptions] = JsonConfiguration(optionHandlers = OptionHandlers.WritesNull)

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


   implicit val searchableUserFormFormat:OFormat[SearchableUserForm] = Json.format[SearchableUserForm]
   implicit val eventFormat: OFormat[Event] = Json.format[Event]
   implicit val userFormat: OFormat[User] = Json.format[User]
   implicit val imageFormat: OFormat[Image] = Json.format[Image]
   implicit val tokenRepFormat: OFormat[TokenRep] = Json.format[TokenRep]
   implicit val tokenRepRegistrationFormat: OFormat[TokenRepRegistration] =
      Json.format[TokenRepRegistration]
   implicit val tokenRepPrivateFormat: OFormat[TokenRepPrivate] =
      Json.format[TokenRepPrivate]

   implicit def tuple2Writes[A,B](implicit a : Writes[A],b: Writes[B]) : Writes[(A, B)] =
      (o: (A, B)) => JsObject(Seq("first" -> a.writes(o._1), "second" -> b.writes(o._2)))

   implicit def tuple2Reads[A,B](implicit a : Reads[A],b: Reads[B]) : Reads[(A, B)] =
      json => (a.reads((json \ "first").get) and b.reads((json \ "second").get)).tupled


}
