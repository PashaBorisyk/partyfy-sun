package util

import java.io.{File, FileWriter}

import com.google.gson.Gson

object Util {

   def writeToFile(body:Any): File ={

      val file = File.createTempFile("prfix","suffix")
      val writer = new FileWriter(file)
      writer.write(new Gson().toJson(body))
      writer.close()
      file

   }

   def asJson(any:Any) = new Gson().toJson(any)

}
