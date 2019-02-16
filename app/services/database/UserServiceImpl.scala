package services.database

import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.database.traits.UserService
import services.traits.{JWTCoder, TokenRep}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  private val jwtCoder: JWTCoder
                               )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserService[Future] {

   private val eventTable = TableQuery[EventDAO]
   private val userRegistrationTable = TableQuery[UserRegistrationDAO]
   private val userEventTable = TableQuery[EventUserDAO]
   private val userTable = TableQuery[UserDAO]
   private val friendsTable = TableQuery[UserUserDAO]
   private val imageTable = TableQuery[HipeImageDAO]

   def getUsersByEventId(eventId: Long, token: TokenRep): Future[Seq[(User, Serializable)]] = {

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

   def checkUserExistence(username: String) = {
      db.run(userTable.filter(_.username === username).map(_.id).exists.result)
   }

   def updateUser(user: User, oldToken: TokenRep): Future[String] = {
      val newToken = jwtCoder.encodePrivate(oldToken.userId, user.username, user.secret)
      db.run(userTable.filter { user => user.id === oldToken.userId && user.username === oldToken.username }.update
      (user.copy(secret = newToken))).map { _ =>
         newToken
      }

   }

   def getFriends(userId: Long, token: TokenRep): Future[Seq[(User, Serializable)]] = {

      val query = (for {
         (user, image) <- userTable joinLeft imageTable on (_.imageId === _.id)
      } yield (user, image)).filter { entry =>
         entry._1.id in {
            friendsTable.filter(_.user_from === userId).map(_.user_to)
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

   def getFriendsIds(userId: Long, token: TokenRep): Future[Seq[Long]] = {
      db.run(friendsTable.filter {
         _.user_from === userId
      }.map(_.user_to).result)
   }

   def findUser(searchString: String, token: TokenRep) = {

      val queries = searchString.split("\\s+").mkString("|")
      val id = token.userId

      val query = sql"""
               SELECT id FROM public.user WHERE (username ~* '#$queries' OR surname ~* '#$queries' OR name ~*
               '#$queries') AND
                id != #$id ORDER BY id DESC
             """.as[Long]

      val execute = query.flatMap { result =>

         (for {
            (user, image) <- userTable joinLeft imageTable on (_.imageId === _.id)
         } yield (user, image)
            ).filter(_._1.id inSet result).result

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

      db.run(execute)

   }

   def getById(id: Long, token: TokenRep): Future[(User, Serializable)] = {

      val execute = (for {
         (user, image) <- userTable joinLeft imageTable on (_.imageId === _.id)
      } yield (user, image)
         ).filter(_._1.id === id).result.head.map {
         eventImageEntry =>
            (eventImageEntry._1, {
               eventImageEntry._2 match {
                  case Some(image) => image
                  case None => None
               }
            })
      }

      db.run(execute)
   }

   def addUserToFriends(userId: Long, token: TokenRep): Future[Int] = {
      db.run(friendsTable += UserUser( token.userId,userId))
   }

   def removeUserFromFriends(userId: Long, token: TokenRep): Future[Int] = {
      db.run(friendsTable.filter { s => s.user_from === userId && s.user_to === userId }.delete)
   }

   def login(username: String, password: String) = {
      db.run(userTable.filter { user => user.username === username }.result.headOption)
         .map {
            case Some(user) =>
               val token = jwtCoder.encodePrivate(user.id,username, password)
               if (token == user.secret) {
                  Some(token)
               }
               else None

            case None => None
         }
   }

}