package controllers

import play.api.test.Helpers.{await, _}

class UserRegistrationControllerImplTest extends BaseTestSuite {

   println(s"Strarting ${classOf[UserRegistrationControllerImplTest].getSimpleName}")

   "registerUser" in {

      usernames.foreach { username =>
         val firstToken = createRegistration(username)
         val secondToken = confirmRegistrationAndCreateUser(firstToken)
         println(secondToken)
      }

   }

   def createRegistration(username:String) = {

      val loginFirstUrl = s"$baseUrl/user_register/create_registration/"
      val firstRequest = wsClient.url(loginFirstUrl).addQueryStringParameters(
         ("username", username),
         ("secret", "password"),
         ("email", s"$username@gmail.com")
      ).execute("POST")

      val firstResult = await(firstRequest)
      firstResult.status mustBe CREATED
      val firstToken = firstResult.body
      firstToken
   }

   def confirmRegistrationAndCreateUser(firstToken: String) = {

      val loginSecondUrl = s"$baseUrl/user_register/confirmRegistration_and_create_user/"
      val secondRequest = wsClient.url(loginSecondUrl).addQueryStringParameters(
         "registrationToken" -> firstToken
      ).execute("POST")

      val secondResult = await(secondRequest)
      secondResult.status mustBe OK
      val secondToken = secondResult.body
      secondToken
   }

}
