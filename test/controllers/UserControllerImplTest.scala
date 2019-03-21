package controllers

import models.persistient.{User, UserSex}
import play.api.test.Helpers._
import util.Util

import scala.util.Random

class UserControllerImplTest extends BaseTestSuite {

   println(s"Strarting ${classOf[UserControllerImplTest].getSimpleName}")

   final val eventUrl = s"$baseUrl/user"
   final val eventId = 0

   "checkUserExistence" in {
      usernames.foreach { username =>
         val checkUserExistenceUrl = s"$baseUrl/user/check_existence/$randomUsername/"
         val request = wsClient.url(checkUserExistenceUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val result = await(request)
         println(result.body)
         val isSuccessful = result.status == OK || result.status == NO_CONTENT
         isSuccessful mustBe true
      }
   }

   "updateUser" in {

      usernames.foreach { username =>
         val updateuserUrl = s"$baseUrl/user/update/"
         val request = wsClient.url(updateuserUrl).withHttpHeaders(
            AUTHORIZATION -> token(username),
            CONTENT_TYPE -> JSON
         ).put(
            Util.asJson(getUser(username))
         )

         val result = await(request)
         println(result.body)
         result.status mustBe ACCEPTED
         setToken(username,result.body)
      }
   }

   "findUser" in {

      usernames.foreach { username =>
         val findUser = s"$baseUrl/user/find/"
         val request = wsClient.url(findUser).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).withQueryStringParameters(
            "query" -> randomUsername.substring(2)
         ).get()

         val response = await(request)
         println(response.body)
         val isSuccess = response.status == 204 || response.status == 200
         isSuccess mustBe true
      }

   }

   "createUsersRelation" in {

      usernames.foreach { username =>
         val addUserToFriendsUrl = s"$baseUrl/user/create_users_relation/"
         val request = wsClient.url(addUserToFriendsUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).withQueryStringParameters(
            "user_id" -> randomUserId.toString,
            "relation_type" -> randomUsersRelationType.toString
         ).execute(PUT)

         val response = await(request)
         println(response.body)
         val isOk = response.status == OK || response.status == NOT_MODIFIED || response.body.contains("User is not allowed to relate to himself")
         isOk mustBe true
      }
   }

   "removeUsersRelation" in {
      usernames.foreach { username =>

         val removeUserFromFriendsUrl = s"$baseUrl/user/remove_users_relation/"
         val request = wsClient.url(removeUserFromFriendsUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).withQueryStringParameters(
            "user_id" -> randomUserId.toString
         ).execute(DELETE)

         val response = await(request)
         println(response.body)
         val isOk = response.status == ACCEPTED || response.status == NOT_MODIFIED
         isOk mustBe true
      }

   }

   "getById" in {

      usernames.foreach { username =>
         val getByIdUrl = s"$baseUrl/user/get_by_id/$randomUserId/"

         val request = wsClient.url(getByIdUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()
         val response = await(request)
         println(response.body)
         val isOk = response.status == OK || response.status == NO_CONTENT
         isOk mustBe true
      }
   }

   "getFriends" in {

      usernames.foreach { username =>
         val getFriendsUrl = s"$baseUrl/user/get_friends/$randomUserId/"

         val request = wsClient.url(getFriendsUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()
         val response = await(request)

         println(response.body)
         val isOk = response.status == OK || response.status == NO_CONTENT
         isOk mustBe true
      }

   }

   "getFriendsIds" in {
      usernames.foreach { username =>

         val getFriendsIdsUrl = s"$baseUrl/user/get_friends_ids/$randomUserId/"
         val request = wsClient.url(getFriendsIdsUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()
         val response = await(request)

         println(response.body)
         val isOk = response.status == OK || response.status == NO_CONTENT
         isOk mustBe true
      }
   }

   "getUsersByEvent" in {

      usernames.foreach { username =>
         val getUsersByEventUrl = s"$baseUrl/user/get_users_by_event_id/$eventId/"
         val request = wsClient.url(getUsersByEventUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()
         val response = await(request)

         println(response.body)
         val isOk = response.status == OK || response.status == NO_CONTENT
         isOk mustBe true
      }
   }

   def getUser(username:String) = User(
      username = username,
      name = username.substring(0,4),
      surname = username.substring(4,username.length-1),
      sex = UserSex.values()(new Random().nextInt(2)),
      status = "Hi! Im " + username,
      latitude = randomCoordinate,
      longitude = randomCoordinate,
      email = s"$username@gmail.com"
   )


}
