package dao

import dao.traits.UserDAO
import javax.inject.Inject
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.traits.{JWTCoder, TokenRepresentation}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserDAOImpl @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  private val jwtCoder: JWTCoder
                               )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserDAO[Future] {

   private val eventTable = TableQuery[EventTable]
   private val userRegistrationTable = TableQuery[UserRegistrationTable]
   private val userEventTable = TableQuery[EventToUserTable]
   private val userTable = TableQuery[UserTable]
   private val friendsTable = TableQuery[UserToUserTable]
   private val imageTable = TableQuery[ImageTable]

   def getUsersByEventId(eventId: Long, token: TokenRepresentation): Future[Seq[(User, Serializable)]] = {

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

   def updateUser(user: User, oldToken: TokenRepresentation): Future[String] = {
      val newToken = jwtCoder.encodePrivate(oldToken.userId, user.username, user.secret)
      db.run(userTable.filter { user => user.id === oldToken.userId && user.username === oldToken.username }.update
      (user.copy(secret = newToken))).map { _ =>
         newToken
      }

   }

   def getFriends(userId: Long, token: TokenRepresentation): Future[Seq[(User, Serializable)]] = {

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

   def getFriendsIds(userId: Long, token: TokenRepresentation): Future[Seq[Long]] = {
      db.run(friendsTable.filter {
         _.user_from === userId
      }.map(_.user_to).result)
   }

   def findUser(searchString: String, token: TokenRepresentation) = {

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

   def getById(id: Long, token: TokenRepresentation): Future[(User, Serializable)] = {

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

   def addUserToFriends(userId: Long, token: TokenRepresentation): Future[Int] = {
      db.run(friendsTable += UserToUser( token.userId,userId))
   }

   def removeUserFromFriends(userId: Long, token: TokenRepresentation): Future[Int] = {
      db.run(friendsTable.filter { s => s.user_from === userId && s.user_to === userId }.delete)
   }

   def login(username: String, password: String) = {
      db.run(userTable.filter { user => user.username === username }.result.headOption)
         .map {
            case Some(user) =>
               val token = jwtCoder.decodePrivate(user.secret)
               if (token.secret == password) {
                  Some(user.secret)
               }
               else None

            case None => None
         }
   }

}