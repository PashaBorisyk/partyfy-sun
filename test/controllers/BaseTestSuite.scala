package controllers

import models.persistient.{Event, EventPrivacyType, UsersRelationType}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers
import play.api.test.Helpers.{await, _}
import util.Util

import scala.collection.mutable.{Map => MMap}
import scala.util.Random

abstract class BaseTestSuite extends PlaySpec with GuiceOneServerPerSuite {

   final val wsClient = app.injector.instanceOf[WSClient]
   final val myPublicAddress = s"localhost:$port"
   final val baseUrl = s"http://$myPublicAddress"

   private final val eventUrl = s"$baseUrl/event"


   //   final val inMemoryDatabaseConf = Map(
   //      "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
   //      "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
   //      "slick.dbs.default.db.driver" -> "org.h2.Driver",
   //      "slick.dbs.default.db.url" -> "jdbc:h2:mem:play",
   //      "slick.dbs.default.db.keepAliveConnection" -> "true",
   //      "slick.dbs.default.db.user" -> "pashaborisyk",
   //      "slick.dbs.default.db.password" -> "Puschinarij1"
   //   )

   override def fakeApplication() = new GuiceApplicationBuilder().configure(Helpers.inMemoryDatabase()).build()

   final val usernames = Array(
      "pashaborisyk",
      "nikiryb",
      "jerryearmark",
      "ridingrudy",
      "palpitatebogeyman",
      "buffingfaceless",
      "trespasscobra",
      "tartegroin",
      "careensag",
      "gatzplaymaker",
      "countyfootprints",
      "deathamstring",
      "fanfarebunch",
      "anthemnaive",
      "cookblink",
      "message-processor"
   )

   final lazy val events = {

      val usernameEventMap = MMap[String,Event]()
      usernames.foreach { username =>
         val array = new java.util.ArrayList[Int]()
         val event = createEvent(username)
         for (k <- usernames.indices if new Random().nextBoolean()) {
            array.add(k)
         }
         val content = Util.asJson(Array[Any](event, array))
         println(content)
         val createEventUrl = s"$eventUrl/create/"
         val request = wsClient.url(createEventUrl).withHttpHeaders(
            AUTHORIZATION -> token(username),
            CONTENT_TYPE -> JSON
         ).post(content)

         val result = await(request)
         println(result.body)
         val isSuccess = result.status == CREATED || result.status == ACCEPTED
         isSuccess mustBe true
         usernameEventMap(username) = event.copy(id = result.body.toLong)
      }

      usernameEventMap

   }

   def createEvent(username: String) = Event(
      maxMembers = new Random().nextInt(usernames.length),
      description = s"Created by me : $username",
      privacy = EventPrivacyType.values()(new Random().nextInt(EventPrivacyType.values().length)),
      ownerID = usernames.indexOf(username),
      country = "PL",
      city = "Warsaw",
      street = "Iganska 20",
      latitude = randomCoordinate,
      longitude = randomCoordinate,
      ownerUsername = username
   )

   def getEvent(username:String) = {
      events(username)
   }

   def randomEventID = {
      val i = new Random().nextInt(events.size)
      val array = events.values.toArray
      array(i).id
   }

   private lazy val usernamesWithTokens = {

      var userTokenMap = MMap("" -> "")
      usernames.foreach { username =>

         var result = login(username)
         if (result.status == NO_CONTENT) {
            result = login(s"$username-nick")
         }
         userTokenMap = userTokenMap + (username -> result.body)

      }
      userTokenMap

   }

   private def login(username: String) = {
      val loginUrl = s"$baseUrl/user/login/"
      val request = wsClient.url(loginUrl).addQueryStringParameters(
         ("username", username),
         ("secret", "password")
      ).get()

      await(request)
   }

   def token(username: String) = {
      usernamesWithTokens(username)
   }

   def setToken(username: String, token: String) = {
      usernamesWithTokens += (username -> token)
   }

   def randomUserID = random + 1

   def randomUsername = usernames(random)

   def randomCoordinate = new Random().nextDouble() * 180

   def randomUsersRelationType = UsersRelationType.values()( new Random().nextInt(UsersRelationType.values().length))

   private def random = new Random().nextInt(usernames.length - 1)

}
