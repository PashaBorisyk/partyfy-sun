package dao.sql.tables.workers

import dao.sql.tables._
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

@Singleton
class SchemasCreator @Inject()(
                                 protected val dbConfigProvider: DatabaseConfigProvider)(
                                 implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger("application")

   lazy val schema = TableQuery[ImageTable].schema
   lazy val schema2 = TableQuery[EventTable].schema
   lazy val schema4 = TableQuery[EventToUserTable].schema
   lazy val schema5 = TableQuery[UserTable].schema
   lazy val schema6 = TableQuery[UserToUserRelationTable].schema
   lazy val schema7 = TableQuery[UserRegistrationTable].schema
   lazy val schema8 = TableQuery[UserToImageTable].schema

   lazy val schemaArr = Array(
      schema,
      schema2,
      schema4,
      schema5,
      schema6,
      schema7,
      schema8
   )

   db.run(
      //      DBIO.seq(schemaArr.map(schema => schema.dropIfExists): _*)
      DBIO.seq(schemaArr.map(schema => schema.create): _*)
   ).recover {
         case e: Exception => logger.debug("Error while creating tables : ", e)
   }
}
