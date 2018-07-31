package db.services

import implicits.implicits._
import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class UserServiceImpl @Inject()(
                             protected val dbConfigProvider: DatabaseConfigProvider,
                             private val jwtCoder: JWTCoder
                           )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] {
   
   private val eventTable = TableQuery[EventDAO]
   private val userEventTable = TableQuery[EventUserDAO]
   private val userTable = TableQuery[UserDAO]
   private val userRegistrationTable = TableQuery[UserRegistrationDAO]
   private val friendsTable = TableQuery[UserUserDAO]
   private val imageTable = TableQuery[HipeImageDAO]
   
   def getUsersByEventId(eventId: Long)(implicit request: Request[_]) = {
      
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
   
   def checkUserExistence(nickName: String)(implicit request: Request[_]) = {
      db.run(userTable.filter(_.username === nickName).exists.result)
   }
   
   def registerUserStepOne(username:String,emailAddress:String,password:String)(implicit request: Request[_]) = {
      
      val publicToken = jwtCoder.encodePublic(username,emailAddress)
      val userRegistration = UserRegistration(
         username = username,
         password = password,
         emailAddress = emailAddress,
         publicToken = publicToken
      )
      db.run(userRegistrationTable+=userRegistration)
   }
   
   def registerUserStepTwo(publicToken:String)(implicit request: Request[_]) = {
      
      db.run()
      
      db.run(query += user).map{ id =>
         jwtCoder.encodePrivate((username,password,id))
      }
      db.run(userRegistrationTable.filter(_.publicToken === publicToken).result.headOption)
   }
   
   def confirmRegistration(registrationId:Long) ={
      db.run(userRegistrationTable.filter(_.id === registrationId).map{
         userRegistration =>
            db.run(userRegistrationTable.update())
      })
   }
   
   def deleteUserRegistration(registrationId:Long) = {
      db.run(userRegistrationTable.filter(_.id === registrationId).delete)
   }
   
   def updateUser(user: User)(implicit request: Request[_]) = {
      db.run(userTable.filter(_.id === user.id).update(user))
   }
   
   def getFriends(userId: Long)(implicit request: Request[_]) = {
      
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
   
   def getFriendsIds(userId: Long)(implicit request: Request[_]) = {
      db.run(friendsTable.filter{_.userId1 === userId}.map(_.userId2).result)
   }
   
   def findUser(userId: Long, searchString: String)(implicit request: Request[_]): Future[Seq[(User, Serializable with Product)]] = {
      
      val queries = searchString.split("\\s+").mkString("|")
      val id = userId.toString
      
      val query = sql"""
               SELECT id FROM public.user WHERE nickname ~* '#$queries' OR surname ~* '#$queries' OR name ~* '#$queries' AND id != #$id ORDER BY id DESC
             """.as[Long]
      
      val idSet = Await.result(db.run(query),10.seconds)
      
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
   
   def getById(id:Long)(implicit request: Request[_]) = {
      val userQuery = userTable.filter{_.id === id}
      val user = db.run(userQuery.result.head)
      val image = db.run(imageTable.filter{_.id in userQuery.map(_.imageId)}.result.head)
      user.zip(image)
   }
   
   def addUserToFriends(userId: Long, advancedUserId: Long)(implicit request: Request[_]) = {
      db.run(friendsTable += UserUser(userId,advancedUserId))
   }
   
   def removeUserFromFriends(userId: Long, advancedUserId: Long)(implicit request: Request[_]) = {
      db.run(friendsTable.filter{s=> s.userId1 === userId && s.userId2 === advancedUserId}.delete)
   }
   
   def login(username:String, password:String)(implicit request: Request[_]) = {
      db.run(userTable.filter{user => user.username === username && user.password === password}.map(_.id).result.head)
         .map{
         userId =>
            jwtCoder.encodePrivate((username,password,userId))
      }
   }
   
}