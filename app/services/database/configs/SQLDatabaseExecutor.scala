package services.database.configs

import javax.inject.{Inject, Singleton}
import models.persistient._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.ControllerComponents
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

@Singleton
class SQLDatabaseExecutor @Inject()(
                           protected val dbConfigProvider: DatabaseConfigProvider,
                           cc: ControllerComponents
                        )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] {

   private val logger = Logger(this.getClass)

   lazy val schema = TableQuery[ImageTable].schema
   lazy val schema2 = TableQuery[ChatMessageUserTable].schema
   lazy val schema3 = TableQuery[EventTable].schema
   lazy val schema4 = TableQuery[EventHipeImageDAO].schema
   lazy val schema5 = TableQuery[EventToUserTable].schema
   lazy val schema6 = TableQuery[EventNewsTable].schema
   lazy val schema7 = TableQuery[OfflineStoreChatMessagesTable].schema
   lazy val schema8 = TableQuery[OfflineStoreTable].schema
   lazy val schema9 = TableQuery[UserTable].schema
   lazy val schema10 = TableQuery[UserToUserTable].schema
   lazy val schema11 = TableQuery[UserRegistrationTable].schema
   
   lazy val schemaArr = Array(
      schema, schema2,
      schema3, schema4,
      schema5, schema6,
      schema7, schema8,
      schema9, schema10,
      schema11
   )

   db.run(
      DBIO.seq(schemaArr.map(_.create).toArray:_*)
   ).recover{
      case e:Exception => logger.debug("Error while creating tables : ", e)
   }
}