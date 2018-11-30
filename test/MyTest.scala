
import com.google.gson.Gson
import org.junit.Test

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