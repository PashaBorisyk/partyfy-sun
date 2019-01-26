package db.services

import db.services.interfaces.UserService
import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Request
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  private val jwtCoder: JWTCoder
                               )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserService[Future] {

   private lazy val eventTable = TableQuery[EventDAO]
   private lazy val userRegistrationTable = TableQuery[UserRegistrationDAO]
   private val userEventTable = TableQuery[EventUserDAO]
   private val userTable = TableQuery[UserDAO]
   private val friendsTable = TableQuery[UserUserDAO]
   private val imageTable = TableQuery[HipeImageDAO]

   def getUsersByEventId(eventId: Long)(implicit request: Request[_]) = {

      val query = (for {
         (user, image) <- userTable joinLeft imageTable on (_.imageId === _.id)
      } yield (user, image)).filter { entry => entry._1.id in userEventTable.filter { s => s.eventId === eventId }.map(_.userId) }

      db.run(query.result).map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }
      }

   }

   def checkUserExistence(username: String)(implicit request: Request[_]) = {
      db.run(userTable.filter(_.username === username).exists.result)
   }

   def updateUser(user: User)(implicit request: Request[_]) = {
      db.run(userTable.filter(_.id === user.id).update(user))
   }

   def getFriends(userId: Long)(implicit request: Request[_]) = {

      val query = (for {
         (user, image) <- userTable joinLeft imageTable on (_.imageId === _.id)
      } yield (user, image)).filter { entry =>
         entry._1.id in {
            friendsTable.filter(_.userId1 === userId).map(_.userId2)
         }
      }.sortBy(_._1.id.desc)

      db.run(query.result).map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }
      }

   }

   def getFriendsIds(userId: Long)(implicit request: Request[_]) = {
      db.run(friendsTable.filter {
         _.userId1 === userId
      }.map(_.userId2).result)
   }

   def findUser(userId: Long, searchString: String)(implicit request: Request[_]) = {

      val queries = searchString.split("\\s+").mkString("|")
      val id = userId.toString

      val query = sql"""
               SELECT id FROM public.user WHERE nickname ~* '#$queries' OR surname ~* '#$queries' OR name ~* '#$queries' AND id != #$id ORDER BY id DESC
             """.as[Long]

      db.run(query).flatMap { result =>

         db.run((
            for {
               (user, image) <- userTable joinLeft imageTable on (_.imageId === _.id)
            } yield (user, image)
            ).filter(_._1.id inSet result).result)

      }.map {
         entry =>
            entry.map { eventImageEntry =>
               (eventImageEntry._1, {
                  eventImageEntry._2 match {
                     case Some(image) => image
                     case None => None
                  }
               })
            }
      }

   }

   def getById(id: Long)(implicit request: Request[_]) = {
      val userQuery = userTable.filter {
         _.id === id
      }
      db.run(userQuery.result.head).zip(
         db.run(imageTable.filter {
            _.id in userQuery.map(_.imageId)
         }.result.head))
   }

   def addUserToFriends(userId: Long, advancedUserId: Long)(implicit request: Request[_]) = {
      db.run(friendsTable += UserUser(userId, advancedUserId))
   }

   def removeUserFromFriends(userId: Long, advancedUserId: Long)(implicit request: Request[_]) = {
      db.run(friendsTable.filter { s => s.userId1 === userId && s.userId2 === advancedUserId }.delete)
   }

   def login(username: String, password: String)(implicit request: Request[_]) = {
      db.run(userTable.filter { user => user.username === username && user.password === password }.map(_.id).result.head)
         .map {
            userId =>
               jwtCoder.encodePrivate((username, password, userId))
         }
   }

}