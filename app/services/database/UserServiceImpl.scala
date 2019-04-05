package services.database

import actors.UserRelationCreatedRecord
import akka.actor.ActorRef
import dao.traits.UserDAO
import javax.inject.{Inject, Named}
import models.TokenRepPrivate
import models.persistient._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.database.traits.UserService
import services.traits.JWTCoder
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
                                  @Named("kafka-producer") userActionProducer: ActorRef,
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  private val userDAO: UserDAO[Future],
                                  private val jwtCoder: JWTCoder)(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile]
      with UserService[Future] {

   def getUsersByeventID(eventID: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getUsersByeventID(eventID)
   }

   def checkUserExistence(username: String) = {
      userDAO.checkUserExistence(username)
   }

   def clientUpdateUser(user: User)(implicit token: TokenRepPrivate) = {
      val newTokenRep = TokenRepPrivate(
         userID = token.userID,
         email = user.email,
         username = user.username,
         secret = token.secret
      )
      val newToken = jwtCoder.encode(newTokenRep)
      userDAO.clientUpdateUser(
         user.copy(id = token.userID, token = newToken, state = UserState.ACTIVE))
   }

   def getFriends(userID: Int)(implicit token: TokenRepPrivate) = {
      userDAO.getFriends(userID)
   }

   def getFriendsIds(userID: Int)(implicit token: TokenRepPrivate) = {
      userDAO.getFriendsIds(userID)
   }

   def findUser(searchString: String)(implicit token: TokenRepPrivate) = {
      userDAO.findUser(token.userID, searchString)
   }

   def getById(userID: Int)(implicit token: TokenRepPrivate) = {
      userDAO.getById(userID)
   }

   def createUsersRelation(userID: Int, relationType: String)(
      implicit token: TokenRepPrivate) = {
      if (token.userID == userID)
         throw new RuntimeException("User is not allowed to relate to himself.")
      val relation = UsersRelationType.valueOf(relationType)
      val userToUser = UserToUserRelation(token.userID, userID, relation)
      val createAction = userDAO.createUsersRelation(userToUser)
      createAction.onComplete { insertedRows =>
         if (insertedRows.getOrElse(0) != 0) {
            userActionProducer ! UserRelationCreatedRecord(token.userID,
               token.username,
               userID,
               relation)
         }
      }
      createAction
   }

   def removeUsersRelation(userID: Int)(implicit token: TokenRepPrivate) = {
      val userToUser = UserToUserRelation(token.userID, userID)
      userDAO.removeUsersRelation(userToUser)
   }

   def login(username: String, secret: String) = {
      userDAO.getTokenByuserID(username).map {
         case Some(token) if jwtCoder.decodePrivateToken(token).secret == secret =>
            Some(token)
         case _ => None
      }
   }

   override def getusersIDssByeventID(eventID: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getusersIDssByeventID(eventID:Long)
   }

}
