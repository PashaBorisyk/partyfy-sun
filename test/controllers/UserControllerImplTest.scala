package controllers

import models.User
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import util.Util

class UserControllerImplTest extends PlaySpec with GuiceOneServerPerSuite {

   println(s"Strarting ${classOf[UserControllerImplTest].getSimpleName}")

   final val wsClient = app.injector.instanceOf[WSClient]
   final val myPublicAddress = s"localhost:$port"
   final val baseUrl = s"http://$myPublicAddress"
   final val eventUrl = s"$baseUrl/user"
   final val userId = 1
   final val eventId = 0

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

   "checkUserExistence" in {

      val checkUserExistanceUrl = s"$baseUrl/user/check_existence/pashaborisyk"
      val request = wsClient.url(checkUserExistanceUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val result = await(request)
      println(result.body)
      result.status mustBe OK

   }

   "updateUser" in {

      val updateuserUrl = s"$baseUrl/user/update/"
      val request = wsClient.url(updateuserUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).put(
         Util.asJson(getUser)
      )

      val result = await(request)
      println(result.body)
      result.status mustBe ACCEPTED
   }

   "findUser" in {

      val findUser = s"$baseUrl/user/find/"
      val query = "pashab"
      val request = wsClient.url(findUser).withHttpHeaders(
         AUTHORIZATION->token
      ).withQueryStringParameters(
         "query"->query
      ).get()

      val response = await(request)
      println(response.body)
      val isSuccess = response.status == 204 || response.status == 200
      isSuccess mustBe true

   }

   "addUserToFriends" in {

      val addUserToFriendsUrl = s"$baseUrl/user/add_user_to_friends/"
      val request = wsClient.url(addUserToFriendsUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).withQueryStringParameters(
         "user_id" -> 0L.toString
      ).execute(POST)

      val response = await(request)
      println(response.body)
      response.status mustBe OK

   }

   "removeUserFromFriends" in {

      val removeUserFromFriendsUrl = s"$baseUrl/user/remove_user_from_friends/"
      val request = wsClient.url(removeUserFromFriendsUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).withQueryStringParameters(
         "user_id" -> 0L.toString
      ).execute(DELETE)

      val response = await(request)
      println(response.body)
      response.status mustBe ACCEPTED

   }

   "getById" in {

      val getByIdUrl = s"$baseUrl/user/get_by_id/$userId/"

      val request = wsClient.url(getByIdUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()
      val response = await(request)
      println(response.body)
      response.status mustBe OK

   }

   "getFriends" in {

      val getFriendsUrl = s"$baseUrl/user/get_friends/$userId/"

      val request = wsClient.url(getFriendsUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()
      val response = await(request)

      println(response.body)
      response.status mustBe OK

   }

   "getFrindsIds" in {

      val getFriendsIdsUrl = s"$baseUrl/user/get_friends_ids/$userId/"
      val request = wsClient.url(getFriendsIdsUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()
      val response = await(request)

      println(response.body)
      response.status mustBe OK

   }

   "getUsersByEvent" in {

      val getUsersByEventUrl = s"$baseUrl/user/get_users_by_event_id/$eventId/"
      val request = wsClient.url(getUsersByEventUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()
      val response = await(request)

      println(response.body)
      response.status mustBe OK

   }

   lazy val getUser = User(
      username = "pashaborisyk",
      secret = "Puschinarij1_",
      name = "pasha",
      surname = "borisyk",
      isMale = true,
      isOnline = true,
      status = "Hipe application creator",
      latitude = 123.23,
      longitude = 321.123,
      email = "pashaborisyk@gmail.com"
   )


}
