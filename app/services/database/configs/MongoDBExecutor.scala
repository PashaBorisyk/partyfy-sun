package services.database.configs

import javax.inject.{Inject, Singleton}
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.{Configuration, Logger}


@Singleton
class MongoDBExecutor @Inject()(
                                  configuration: Configuration
                               ) {

   private val logger = Logger(this.getClass)

   private lazy val db: MongoDatabase = {
      logger.debug("mongo db client creating...")
      val uri = configuration.get[String]("mongo.uri")
      System.setProperty("org.mongodb.async.type", "netty")
      val database = MongoClient(uri).getDatabase(configuration.get[String]("mongo.db"))
      logger.debug(s"Mongo db client created : $database")
      database
   }

}