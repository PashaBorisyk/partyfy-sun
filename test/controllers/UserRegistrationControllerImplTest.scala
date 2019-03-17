package controllers

import play.api.test.Helpers.await

import play.api.libs.ws.WSClient
import play.api.test.Helpers._

class UserRegistrationControllerImplTest extends BaseTestSuite {

   println(s"Strarting ${classOf[UserRegistrationControllerImplTest].getSimpleName}")

   private final val wsClient = app.injector.instanceOf[WSClient]
   private final val myPublicAddress = s"localhost:$port"
   private final val baseUrl = s"http://$myPublicAddress"
   private final val username = "pashaborisyk"

   "registerUser" in {

      val firstToken = createRegistration()
      val secondToken = confirmRegistrationAndCreateUser(firstToken)

      println(secondToken)

   }

   def createRegistration() = {

      val loginFirstUrl = s"$baseUrl/user_register/create_registration/"
      val firstRequest = wsClient.url(loginFirstUrl).addQueryStringParameters(
         ("username", username),
         ("secret", "Puschinarij1"),
         ("email",s"$username@gmail.com")
      ).execute("POST")

      val firstResult = await(firstRequest)
      firstResult.status mustBe CREATED
      val firstToken = firstResult.body
      firstToken
   }

   def confirmRegistrationAndCreateUser(firstToken:String ) = {

      val loginSecondUrl = s"$baseUrl/user_register/confirmRegistration_and_create_user/"
      val secondRequest = wsClient.url(loginSecondUrl).addQueryStringParameters(
         "registrationToken"->firstToken
      ).execute("POST")

      val secondResult = await(secondRequest)
      secondResult.status mustBe OK
      val secondToken = secondResult.body
      secondToken
   }

}
