package controllers

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import util.Util

import scala.util.Random

class EventControllerTest extends BaseTestSuite {

   override def fakeApplication() = new GuiceApplicationBuilder().configure().build()

   println(s"Strarting ${classOf[EventControllerTest].getSimpleName}")

   final val eventUrl = s"$baseUrl/event"


   "test" in {
      val testUrl = s"$eventUrl/test/"
      usernames.foreach { username =>

         val result = await(wsClient.url(testUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get())
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

   "addMemberToEvent" in {
      usernames.foreach { username =>
         val addMemberToEventUrl = s"$eventUrl/add_member/"
         val request = wsClient.url(addMemberToEventUrl).addQueryStringParameters(
            ("event_id", randomEventId.toString),
            ("user_id", randomUserId.toString),
            ("advanced_user_id", randomEventId.toString)
         ).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).execute("PUT")

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == ACCEPTED || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

   "removeMember" in {
      val removeMemberUrl = s"$eventUrl/remove_member/"
      usernames.foreach { username =>
         val request = wsClient.url(removeMemberUrl).addQueryStringParameters(
            ("user_id", randomUserId.toString),
            ("advanced_user_id", randomUserId.toString),
            ("event_id", randomEventId.toString)
         ).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).delete()

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == ACCEPTED || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }


   "cancelEvent" in {
      usernames.foreach { username =>
         val cancelEvent = s"$eventUrl/cancel/"
         val request = wsClient.url(cancelEvent).addQueryStringParameters(
            ("user_id", randomUserId.toString),
            ("event_id", randomEventId.toString)
         ).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).delete()

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

   "getEvents" in {
      usernames.foreach { username =>

         val getEventsUrl = s"$eventUrl/get/"
         val request = wsClient.url(getEventsUrl).addQueryStringParameters(
            ("user_id", randomUserId.toString),
            ("latitude", randomCoordinate.toString),
            ("longitude", randomCoordinate.toString),
            ("last_read_event_id", randomEventId.toString)
         ).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

   "getByMember" in {

      usernames.foreach { username =>
         val getByMemberUrl = s"$eventUrl/get_by_member_id/$randomUserId/"
         val request = wsClient.url(getByMemberUrl).addQueryStringParameters(
            ("user_id", "1")
         ).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true

      }
   }

   "getByOwner" in {
      usernames.foreach { username =>
         val getByMemberIdUrl = s"$eventUrl/get_by_owner_id/$randomUserId/"
         val request = wsClient.url(getByMemberIdUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

   "getById" in {

      usernames.foreach { username =>
         val getByIdUrl = s"$eventUrl/get_by_id/$randomEventId/"
         val request = wsClient.url(getByIdUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true
      }

   }

   "updateEvent" in {

      usernames.foreach { username =>
         val updateEventUrl = s"$eventUrl/update/"
         val request = wsClient.url(updateEventUrl).withHttpHeaders(
            AUTHORIZATION -> token(username),
            CONTENT_TYPE -> JSON
         ).put(Util.asJson(createEvent(username).copy(id = getEvent(username).id)))

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == OK || result.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

}
