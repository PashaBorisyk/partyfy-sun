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

      val loginFirstUrl = s"$baseUrl/user_register/step_one/"
      val firstRequest = wsClient.url(loginFirstUrl).addQueryStringParameters(
         ("username", username),
         ("password", "Puschinarij1")
      ).execute("POST")

      val firstResult = await(firstRequest)
      firstResult.status mustBe CREATED
      val firstToken = firstResult.body

      val loginSecondUrl = s"$baseUrl/user_register/step_two/"
      val secondRequest = wsClient.url(loginSecondUrl).addQueryStringParameters(
         ("username",username),
         ("email",s"$username@gmail.com"),
         ("publicToken",firstToken)
      ).execute("POST")

      val secondResult = await(secondRequest)
      secondResult.status mustBe OK
      val secondToken = secondResult.body

      val loginThirdUrl = s"$baseUrl/user_register/step_three/"
      val thirdRequest = wsClient.url(loginThirdUrl).addQueryStringParameters(
         ("publicTokenTwo",secondToken)
      ).execute("POST")

      val thirdResult = await(thirdRequest)
      thirdResult.status mustBe OK
      println(thirdResult.body)

   }

}
