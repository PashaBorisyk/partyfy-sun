package controllers

import models.persistient.Event
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import util.Util

class EventControllerTest extends BaseTestSuite {

   override def fakeApplication() = new GuiceApplicationBuilder().configure().build()

   println(s"Strarting ${classOf[EventControllerTest].getSimpleName}")

   final val wsClient = app.injector.instanceOf[WSClient]
   final val myPublicAddress = s"localhost:$port"
   final val baseUrl = s"http://$myPublicAddress"
   final val eventUrl = s"$baseUrl/event"

   lazy val token: String = {

      val loginUrl = s"$baseUrl/user/login/"
      val request = wsClient.url(loginUrl).addQueryStringParameters(
         ("username", "pashaborisyk"),
         ("password", "Puschinarij1")
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK
      result.body

   }

   "test" in {
      val testUrl = s"$eventUrl/test/"
      val result = await(wsClient.url(testUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get())
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true
   }

   "addMemberToEvent" in {
      val addMemberToEventUrl = s"$eventUrl/add_member/"
      val request = wsClient.url(addMemberToEventUrl).addQueryStringParameters(
         ("event_id", "1"),
         ("user_id", "1"),
         ("advanced_user_id", "1")
      ).withHttpHeaders(
         AUTHORIZATION->token
      ).execute("PUT")

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == ACCEPTED || result.status == NO_CONTENT
      isSuccess mustBe true

   }

   "removeMember" in {
      val removeMemberUrl = s"$eventUrl/remove_member/"
      val request = wsClient.url(removeMemberUrl).addQueryStringParameters(
         ("user_id", "123"),
         ("advanced_user_id", "123"),
         ("event_id", "123")
      ).withHttpHeaders(
         AUTHORIZATION->token
      ).delete()

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == ACCEPTED || result.status == NO_CONTENT
      isSuccess mustBe true
   }

   "createEvent" in {

      val array = new java.util.ArrayList[Int]()
      array.add(1)
      val content = Util.asJson(Array[Any](createEvent(),array))
      println(content)
      val createEventUrl = s"$eventUrl/create/"
      val request = wsClient.url(createEventUrl).withHttpHeaders(
         AUTHORIZATION->token,
         CONTENT_TYPE->JSON
      ).post(content)

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == CREATED || result.status == ACCEPTED
      isSuccess mustBe true

   }

   "cancelEvent" in {

      val cancelEvent = s"$eventUrl/cancel/"
      val request = wsClient.url(cancelEvent).addQueryStringParameters(
         ("user_id", "1"),
         ("event_id", "1")
      ).withHttpHeaders(
         AUTHORIZATION->token
      ).delete()

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true

   }

   "getEvents" in {

      val getEventsUrl = s"$eventUrl/get/"
      val request = wsClient.url(getEventsUrl).addQueryStringParameters(
         ("user_id", "1"),
         ("latitude", "123.123"),
         ("longitude", "123.321"),
         ("last_read_event_id", "456")
      ).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true

   }

   "getByMember" in {

      val getByMemberUrl = s"$eventUrl/get_by_member_id/1/"
      val request = wsClient.url(getByMemberUrl).addQueryStringParameters(
         ("user_id", "1")
      ).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true

   }

   "getByOwner" in {

      val getByMemberIdUrl = s"$eventUrl/get_by_owner_id/1/"
      val request = wsClient.url(getByMemberIdUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true

   }

   "getById" in {

      val getByIdUrl = s"$eventUrl/get_by_id/1/"
      val request = wsClient.url(getByIdUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true

   }

   "updateEvent" in {

      val updateEventUrl = s"$eventUrl/update/"
      val request = wsClient.url(updateEventUrl).withHttpHeaders(
         AUTHORIZATION->token,
         CONTENT_TYPE->JSON
      ).put(Util.asJson(createEvent()))

      val result = await(request)
      println(result.body)
      val isSuccess = result.status == OK || result.status == NO_CONTENT
      isSuccess mustBe true

   }



   private def createEvent() = Event(
      1L,
      1L,
      System.currentTimeMillis() + 1000 * 1000,
      System.currentTimeMillis(),
      10,
      52.220130, 21.012105,
      "pashaborisyk",
      "PL",
      "Warsaw",
      "plac Politechniki",
      "Hipe service presentation event",
      "We will drink a lot of vodka and fuck Putin",
      isPublic = true,
      isForOneGender = false,
      isForMale = false,
      0,
      ""
   )

}
