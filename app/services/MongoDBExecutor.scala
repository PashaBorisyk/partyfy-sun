package services

import com.mongodb.DBCollection
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.{MongoClient, TypeImports}
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import util._


@Singleton
class MongoDBExecutor @Inject()(
                               val configuration: Configuration
                               ) {
   
   private lazy val db: MongoDB = {
      logger.debug("mongo db client creating...")
      val client = MongoClient(
         configuration.get[String]("mongo.host"),
         configuration.get[Int]("mongo.port")
      ).getDB(configuration.get[String]("mongo.db"))
      logger.debug(s"Mongo db client created : $client")
      client
   }

   def forName(collectionName:String): DBCollection = db.getCollection(collectionName)
   def clearCollection(collectionName:String): TypeImports.WriteResult = db(collectionName).remove(MongoDBObject())
   
}