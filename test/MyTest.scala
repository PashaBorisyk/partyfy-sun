import java.util

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Sink, Source}
import models.ChatMessageNOSQL
import org.junit.Test
import implicits.implicits._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.google.gson.Gson
import collection.JavaConversions._
import scala.io.StdIn

case class MCA(s: String = "sdgdfbdfdg")

case class MClass(r: Long = 0, s: String = "asd", l: List[Any] = List(1, 2, MCA("asdf")), rt: MCA = MCA()) extends Serializable

@Test
class MyTest{
   
   @Test
   def asd(): Unit ={
      
      val js = new Gson().toJson(null)
      println(js)
      
   }
   
}