package controllers

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import java.io._


class ImageContorllerTest extends BaseTestSuite {

   override def fakeApplication() = new GuiceApplicationBuilder().configure().build()

   println(s"Strarting ${classOf[ImageContorllerTest].getSimpleName}")

   final val wsClient = app.injector.instanceOf[WSClient]
   final val myPublicAddress = s"localhost:$port"
   final val baseUrl = s"http://$myPublicAddress"
   final val imageUrl = s"$baseUrl/image"
   final val pathToFile = "C:/pashaborisyk/dump/Ultra_Stage.jpg"

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

   "upload" in {

      val imageFile = new File(pathToFile)
      imageFile.exists() mustBe true

      val uploadUrl = s"$imageUrl/upload/?event_id=1"

      val httpClient = HttpClients.createDefault
      val builder = MultipartEntityBuilder.create
      builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN)

      builder.addBinaryBody("part-file", new FileInputStream(imageFile), ContentType.APPLICATION_OCTET_STREAM, imageFile.getName)

      val multipart = builder.build
      val postMethod = new HttpPost(uploadUrl)
      postMethod.setEntity(multipart)
      postMethod.addHeader(AUTHORIZATION,token)
      val response = httpClient.execute(postMethod)
      val responseEntity = response.getEntity
      response.getStatusLine.getStatusCode mustBe CREATED
      println(new BufferedReader(new InputStreamReader(responseEntity.getContent)).readLine())

   }

   "get" in {

      val getUrl = s"$imageUrl/get_by_id/1/"
      val request = wsClient.url(getUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val response = await(request)
      println(response.body)
      response.status mustBe OK

   }

   "getByUserId" in {

      val getByUserIdUrl = s"$imageUrl/get_by_user_id/1/"
      val request = wsClient.url(getByUserIdUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val response = await(request)
      println(response.body)
      val isSuccess = response.status == OK || response.status == NO_CONTENT
      isSuccess mustBe true
   }

   "getByEventId" in {

      val getByEventIdUrl = s"$imageUrl/get_by_event_id/1/"
      val request = wsClient.url(getByEventIdUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).get()

      val response = await(request)
      println(response.body)
      val isSuccess = response.status == OK || response.status == NO_CONTENT
      isSuccess mustBe true

   }

   "delete" in {

      val deletUrl = s"$imageUrl/delete/1/"
      val request = wsClient.url(deletUrl).withHttpHeaders(
         AUTHORIZATION->token
      ).delete()

      val response = await(request)
      println(response.body)
      response.status mustBe ACCEPTED
   }

}
