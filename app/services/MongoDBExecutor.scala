package services

import javax.inject.{Inject, Provider, Singleton}

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.Imports._
import play.api.{Application, Configuration, Play}
import util._


@Singleton
class MongoDBExecutor @Inject()(
                               val configuration: Configuration
                               ) {
   
   private val db: MongoDB = {
      logger.info("mongo db counstructing...")
      MongoClient(
         configuration.get[String]("mongo.host"),
         configuration.get[Int]("mongo.port")
      ).getDB(configuration.get[String]("mongo.db"))
   }
   def forName(collectionName:String) = db.getCollection(collectionName)
   def clearCollection(collectionName:String) = db(collectionName).remove(MongoDBObject())
   
}