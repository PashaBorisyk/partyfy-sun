package controllers

import models.User
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers._

class UserControllerImplTest extends PlaySpec with GuiceOneServerPerSuite {

   println(s"Strarting ${classOf[UserControllerImplTest].getSimpleName}")

   final val wsClient = app.injector.instanceOf[WSClient]
   final val myPublicAddress = s"localhost:$port"
   final val baseUrl = s"http://$myPublicAddress"
   final val eventUrl = s"$baseUrl/user"
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
      val request = wsClient.url(checkUserExistanceUrl).get()

      val result = await(request)
      println(result.body)
      result.status mustBe FOUND

   }

   "updateUser" in {

      val updateuserUrl = s"$baseUrl/user/update"

   }


}
