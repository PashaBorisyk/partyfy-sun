package controllers

import java.io.{File, FileWriter}

import com.google.gson.Gson
import models.Event
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._

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
      val result = await(wsClient.url(testUrl).get())
      println(result.body)
      result.status mustBe OK
   }

   "addMemberToEvent" in {
      val addMemberToEventUrl = s"$eventUrl/add/member/"
      val request = wsClient.url(addMemberToEventUrl).addQueryStringParameters(
         ("event_id", "123"),
         ("user_id", "123"),
         ("advanced_user_id", "123")
      ).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "removeMember" in {
      val removeMemberUrl = s"$eventUrl/remove_member/"
      val request = wsClient.url(removeMemberUrl).addQueryStringParameters(
         ("user_id", "123"),
         ("advanced_user_id", "123"),
         ("event_id", "123")
      ).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK
   }

   "cancelEvent" in {

      val cancelEvent = s"$eventUrl/event/cancel/"
      val request = wsClient.url(cancelEvent).addQueryStringParameters(
         ("user_id", "123"),
         ("event_id", "123")
      ).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "getEvents" in {

      val getEventsUrl = s"$eventUrl/get/"
      val request = wsClient.url(getEventsUrl).addQueryStringParameters(
         ("user_id", "123"),
         ("latitude", "123.123"),
         ("longitude", "123.321"),
         ("last_read_event_id", "456")
      ).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "getByMember" in {

      val getByMemberUrl = s"$eventUrl/get_by_member_id/123"
      val request = wsClient.url(getByMemberUrl).addQueryStringParameters(
         ("user_id", "123")
      ).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "getByOwner" in {

      val getByMemberIdUrl = s"$eventUrl/get_by_owner_id/123"
      val request = wsClient.url(getByMemberIdUrl).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "getById" in {

      val getByIdUrl = s"$eventUrl/get_by_id/123"
      val request = wsClient.url(getByIdUrl).addHttpHeaders(
         ("Authorization", token)
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "updateEvent" in {

      val updateEventUrl = s"$eventUrl/update/"
      val request = wsClient.url(updateEventUrl).addHttpHeaders(
         ("Authorization", token)
      ).put(writeToFile(createEvent()))

      val result = await(request)
      println(result.body)

   }



   private def createEvent() = Event(
      123L,
      321L,
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
      true,
      false,
      false,
      654,
      ""
   )

   private def writeToFile(body:Any): File ={

      val file = File.createTempFile("prfix","suffix")
      val writer = new FileWriter(file)
      writer.write(new Gson().toJson(body))
      writer.close()
      file

   }


}
