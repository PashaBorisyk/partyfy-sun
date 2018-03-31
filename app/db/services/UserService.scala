package db.services

import javax.inject.Inject
import slick.jdbc.PostgresProfile.api._
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import implicits.implicits._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class UserService @Inject()(
                             protected val dbConfigProvider: DatabaseConfigProvider,
                           )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] {
   
   val eventTable = TableQuery[EventDAO]
   val userEventTable = TableQuery[EventUserDAO]
   val userTable = TableQuery[UserDAO]
   val friendsTable = TableQuery[UserUserDAO]
   val imageTable = TableQuery[HipeImageDAO]
   
   def getUsersByEventId(eventId: Long) = {
      
      val query = (for{
         (user,image) <- userTable joinLeft imageTable on (_.imageId === _.id)
      } yield (user,image)).filter { entry => entry._1.id in userEventTable.filter { s => s.eventId === eventId }.map(_.userId) }
   
      db.run(query.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
      
   }
   
   def checkUserExistence(nickName: String) = {
      db.run(userTable.filter(_.nickName === nickName).exists.result)
   }
   
   def registerUser(user: User) = {
      val query = userTable returning userTable.map(_.id)
      db.run(query += user)
   }
   
   def updateUser(user: User) = {
      db.run(userTable.filter(_.id === user.id).update(user))
   }
   
   def getFriends(userId: Long) = {
      
      val query = (for{
         (user,image) <- userTable joinLeft imageTable on (_.imageId === _.id)
      } yield (user,image)).filter { entry => entry._1.id in {
         friendsTable.filter(_.userId1 === userId).map(_.userId2)}
      }.sortBy(_._1.id.desc)
      
      db.run(query.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
      
   }
   
   def getFriendsIds(userId: Long) = {
      db.run(friendsTable.filter{_.userId1 === userId}.map(_.userId2).result)
   }
   
   def findUser(userId: Long, query: String) = {
      
      val queries = query.split("\\s+").mkString("|")
      val id = userId.toString
      
      val q = sql"""
               SELECT id FROM public.user WHERE nickname ~* '#$queries' OR surname ~* '#$queries' OR name ~* '#$queries' AND id != #$id ORDER BY id DESC
             """.as[Long]
      
      val idSet = Await.result(db.run(q),10.seconds)
      
      val dbquery = (
         for {
            (user,image) <- userTable joinLeft imageTable on (_.imageId === _.id)
         } yield (user,image)
      ).filter(_._1.id inSet idSet)
      
      db.run(dbquery.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
      
   }
   
   def getById(id:Long) = {
      val userQuery = userTable.filter{_.id === id}
      val user = db.run(userQuery.result.head)
      val image = db.run(imageTable.filter{_.id in userQuery.map(_.imageId)}.result.head)
      user.zip(image)
   }
   
   def addUserToFriends(userId: Long, advancedUserId: Long) = {
      db.run(friendsTable += UserUser(userId,advancedUserId))
   }
   
   def removeUserFromFriends(userId: Long, advancedUserId: Long) = {
      db.run(friendsTable.filter{s=> s.userId1 === userId && s.userId2 === advancedUserId}.delete)
   }
   
}