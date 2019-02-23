package implicits


import java.util.AbstractMap.SimpleImmutableEntry
import java.util.Properties

import com.google.gson.{Gson, GsonBuilder}
import com.mongodb.BasicDBObject
import com.typesafe.config.{Config, ConfigValue}
import models.ChatMessageNOSQL
import models.persistient.{Event, HipeImage, User}
import org.bson.types.BasicBSONList
import play.api.ConfigLoader
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import slick.jdbc.GetResult
import util.isPrimitiveOrString

import scala.language.implicitConversions

package object implicits {

   private lazy val exposedGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
   private lazy val gson: Gson = new Gson()

   implicit val queryToUser: AnyRef with GetResult[User] = GetResult(r => User(
      r.<<, r.<<,
      r.<<, r.<<,
      r.<<, r.<<,
      r.<<, r.<<,
      r.<<, r.<<,
      r.<<
   ))


   implicit def any2Int(any: Any): Int = Integer.parseInt(any.toString)

   implicit def req2Event(requestBody: AnyContent): Event = gson.fromJson(requestBody.asText.getOrElse("null"), classOf[Event])

   implicit def req2User(requestBody: AnyContent): User = gson.fromJson(requestBody.asText.getOrElse("null"), classOf[User])

   implicit def req2HipeImage(requestBody: AnyContent): HipeImage = gson.fromJson(requestBody.asText.getOrElse("null"), classOf[HipeImage])

   implicit def req2EventMembersTuple(requestBody: AnyContent): (Event, Set[Long]) = {

      implicit val eventFormat = Json.format[Event]
      implicit val modelFormat = Json.format[(Event, Set[Long])]
      val result = Json.fromJson[(Event, Set[Long])](Json.parse(requestBody.asText.getOrElse("null")))
      result.getOrElse(throw new RuntimeException("Failed to parse json"))

   }

   implicit def string2ChatMessage(s: String): ChatMessageNOSQL = gson.fromJson(s, classOf[ChatMessageNOSQL])

   //   @inline implicit def dbObject2Object[T: ClassTag](dBObject: DBObject): T = gson.fromJson(dBObject.toString, classTag[T].runtimeClass)

   implicit class ObjectImplicits[T <: Any](val t: T) {

      implicit def toDBObject(first: Boolean = true): BasicDBObject = {

         val basicDBObject = new BasicDBObject()
         if (t == null)
            return basicDBObject

         if (first)
            t match {
               case _ :: _ => basicDBObject.put("List", serializeList(t.asInstanceOf[List[AnyRef]])); return basicDBObject
               case _: Set[_] => basicDBObject.put("Set", serializeSet(t.asInstanceOf[Set[AnyRef]])); return basicDBObject
               case _: Seq[_] => basicDBObject.put("Seq", serializeSeq(t.asInstanceOf[Seq[AnyRef]])); return basicDBObject
               case _: Array[_] => basicDBObject.put("Array", serializeArray(t.asInstanceOf[Array[AnyRef]])); return basicDBObject
               case _ if isPrimitiveOrString(t) => basicDBObject.put(t.getClass.getSimpleName, t.asInstanceOf[AnyRef])
               case _ => basicDBObject.put(t.getClass.getSimpleName, t.toDBObject(first = false))
            }

         for (i <- t.getClass.getDeclaredFields) {
            i.setAccessible(true)
            val value: AnyRef = i.get(t)
            if (i.getType.isPrimitive || isPrimitiveOrString(value)) {
               basicDBObject.put(i.getName, value)
            }
            else i.get(t) match {
               case _ :: _ => basicDBObject.put(i.getName, serializeList(value.asInstanceOf[List[AnyRef]]));
               case _: Set[_] => basicDBObject.put(i.getName, serializeSet(value.asInstanceOf[Set[AnyRef]]));
               case _: Seq[_] => basicDBObject.put(i.getName, serializeSeq(value.asInstanceOf[Seq[AnyRef]]));
               case _: Array[_] => basicDBObject.put(i.getName, serializeArray(value.asInstanceOf[Array[AnyRef]]));
               case _: Any => basicDBObject.put(i.getName, value.toDBObject(first = false))
            }
         }
         basicDBObject
      }

      def serializeList(t: List[_ <: AnyRef]): BasicBSONList = {
         val basicDBList = new BasicBSONList()

         for (i <- t) {
            if (isPrimitiveOrString(i)) {
               basicDBList.add(i.asInstanceOf[AnyRef])
            }
            else {
               basicDBList.add(i.toDBObject(first = false))
            }
         }
         basicDBList
      }

      def serializeSet(t: Set[_ <: AnyRef]): BasicBSONList = {
         val basicDBList = new BasicBSONList()

         for (i <- t) {
            if (isPrimitiveOrString(i)) {
               basicDBList.add(i.asInstanceOf[AnyRef])
            }
            else {
               basicDBList.add(i.toDBObject(first = false))
            }
         }
         basicDBList
      }

      def serializeSeq(t: Seq[_ <: AnyRef]): BasicBSONList = {
         val basicDBList = new BasicBSONList()

         for (i <- t) {
            if (isPrimitiveOrString(i)) {
               basicDBList.add(i.asInstanceOf[AnyRef])
            }
            else {
               basicDBList.add(i.toDBObject(first = false))
            }
         }
         basicDBList
      }

      def serializeArray(t: Array[_ <: AnyRef]): BasicBSONList = {
         val basicDBList = new BasicBSONList()

         for (i <- t) {
            if (isPrimitiveOrString(i)) {
               basicDBList.add(i.asInstanceOf[AnyRef])
            }
            else {
               basicDBList.add(i.toDBObject(first = false))
            }
         }
         basicDBList
      }

   }

   //   implicit class DBCursorImplicits[+T <: DBCursor](val t: T) {
   //
   //      def map[R](f: (DBObject) => R)(implicit ev$1: DBObject => R): ArrayBuffer[R] = {
   //         val arr = ArrayBuffer[R]()
   //         t.forEach { s =>
   //            arr += f(s)
   //         }
   //         arr
   //      }
   //
   //   }

   implicit class Object2Json[T](val t: T) {
      def toJson: String = if (isPrimitiveOrString(t)) t.toString else gson.toJson(t)

      def toExposedJson: String = exposedGson.toJson(t)

      def run[R](lambda: T => R) = lambda(t)
   }

   implicit class StringImplicits(string: String) {

      @inline def notNullOrEmpty = !(string == null || string.trim.isEmpty)

   }

   implicit val config2Properties = new ConfigLoader[Properties] {
      override def load(config: Config, path: String): Properties = {

         val properties = new Properties()

         config.getConfig(path).entrySet().toArray(Array[SimpleImmutableEntry[String, ConfigValue]]()).foreach { item =>
            properties.put(item.getKey, item.getValue.unwrapped())
         }

         properties
      }
   }

}