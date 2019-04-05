package controllers

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import java.io._

import scala.util.Random


class ImageControllerTest extends BaseTestSuite {

   override def fakeApplication() = new GuiceApplicationBuilder().configure().build()

   println(s"Starting ${classOf[ImageControllerTest].getSimpleName}")

   final val imageUrl = s"$baseUrl/image"
   final val pathToFile = "C:/pashaborisyk/dump/Ultra_Stage.jpg"

   "upload" in {

      usernames.foreach { username =>
         val imageFile = new File(pathToFile)
         imageFile.exists() mustBe true

         val uploadUrl = s"$imageUrl/upload/?event_id=$randomEventID"

         val httpClient = HttpClients.createDefault
         val builder = MultipartEntityBuilder.create
         builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN)

         builder.addBinaryBody("part-file", new FileInputStream(imageFile), ContentType.APPLICATION_OCTET_STREAM, imageFile.getName)

         val multipart = builder.build
         val postMethod = new HttpPost(uploadUrl)
         postMethod.setEntity(multipart)
         postMethod.addHeader(AUTHORIZATION, token(username))
         val response = httpClient.execute(postMethod)
         val responseEntity = response.getEntity
         response.getStatusLine.getStatusCode mustBe CREATED
         println(new BufferedReader(new InputStreamReader(responseEntity.getContent)).readLine())
      }
   }

   "get" in {

      usernames.foreach { username =>
         val getUrl = s"$imageUrl/get_by_id/$randomEventID/"
         val request = wsClient.url(getUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val response = await(request)
         println(response.body)
         val isSuccess = response.status == OK || response.status == NO_CONTENT
         isSuccess mustBe true
      }

   }

   "getByuserID" in {

      usernames.foreach { username =>

         val getByuserIDUrl = s"$imageUrl/get_by_user_id/$randomUserID/"
         val request = wsClient.url(getByuserIDUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val response = await(request)
         println(response.body)
         val isSuccess = response.status == OK || response.status == NO_CONTENT
         isSuccess mustBe true
      }
   }

   "getByeventID" in {

      usernames.foreach { username =>
         val getByeventIDUrl = s"$imageUrl/get_by_event_id/$randomEventID/"
         val request = wsClient.url(getByeventIDUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).get()

         val response = await(request)
         println(response.body)
         val isSuccess = response.status == OK || response.status == NO_CONTENT
         isSuccess mustBe true

      }
   }

   "delete" in {

      usernames.foreach { username =>
         val deleteUrl = s"$imageUrl/delete/${new Random().nextInt(10)}/"
         val request = wsClient.url(deleteUrl).withHttpHeaders(
            AUTHORIZATION -> token(username)
         ).delete()

         val response = await(request)
         println(response.body)
         response.status mustBe ACCEPTED
      }
   }

}
