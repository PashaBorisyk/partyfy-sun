package services

import javax.inject.{Inject, Singleton}

import models._
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
   
   val schema = TableQuery[HipeImageDAO].schema
   val schema2 = TableQuery[ChatMessageUserDAO].schema
   val schema3 = TableQuery[EventDAO].schema
   val schema4 = TableQuery[EventHipeImageDAO].schema
   val schema5 = TableQuery[EventUserDAO].schema
   val schema6 = TableQuery[ChatMessageUserDAO].schema
   val schema7 = TableQuery[EventNewsDAO].schema
   val schema8 = TableQuery[OfflineStoreChatMessagesDAO].schema
   val schema9 = TableQuery[OfflineStoreDAO].schema
   val schema10 = TableQuery[UserDAO].schema
   val schema11 = TableQuery[UserUserDAO].schema
   
   val schemaArr = Array(
      schema, schema2,
      schema3, schema4,
      schema5, schema6,
      schema7, schema8,
      schema9, schema10,
      schema11
   )
   
   for (n <- schemaArr)
      db.run(DBIO.seq(
         n.create
      )).map(s=>println(s)).recover{case e:Exception => e}
   
}