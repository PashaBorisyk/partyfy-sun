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

class UserServiceImpl @Inject()(@Named("kafka-producer") userActionProducer:ActorRef,
                                protected val dbConfigProvider: DatabaseConfigProvider,
                                private val userDAO: UserDAO[Future],
                                private val jwtCoder: JWTCoder
                               )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserService[Future] {

   def getUsersByEventId(eventId: Long)(implicit token: TokenRepPrivate) = {
      userDAO.getUsersByEventId(eventId)
   }

   def checkUserExistence(username: String) = {
      userDAO.checkUserExistence(username)
   }

   def clientUpdateUser(user: User)(implicit token: TokenRepPrivate) = {
      val newTokenRep = TokenRepPrivate(
         userId = token.userId,
         email = user.email,
         username = user.username,
         secret = token.secret
      )
      val newToken = jwtCoder.encode(newTokenRep)
      userDAO.clientUpdateUser(user.copy(id = token.userId,token = newToken,state = UserState.ACTIVE))
   }

   def getFriends(userId: Int)(implicit token: TokenRepPrivate) = {
      userDAO.getFriends(userId)
   }

   def getFriendsIds(userId: Int)(implicit token: TokenRepPrivate) = {
      userDAO.getFriendsIds(userId)
   }

   def findUser(searchString: String)(implicit token: TokenRepPrivate) = {
      userDAO.findUser(token.userId,searchString)
   }

   def getById(userId: Int)(implicit token: TokenRepPrivate) = {
      userDAO.getById(userId)
   }

   def createUsersRelation(userId: Int,relationType: String)(implicit token: TokenRepPrivate) = {
      if(token.userId == userId)
         throw new RuntimeException("User is not allowed to relate to himself.")
      val relation = UsersRelationType.valueOf(relationType)
      val userToUser = UserToUserRelation(token.userId,userId,relation)
      val createAction = userDAO.createUsersRelation(userToUser)
      createAction.onComplete{ insertedRows =>
         if(insertedRows.getOrElse(0) != 0){
            userActionProducer ! UserRelationCreatedRecord(token.userId,token.username,userId,relation)
         }
      }
      createAction
   }

   def removeUsersRelation(userId: Int)(implicit token: TokenRepPrivate) = {
      val userToUser = UserToUserRelation(token.userId,userId)
      userDAO.removeUsersRelation(userToUser)
   }

   def login(username: String, secret: String) = {
      userDAO.getTokenByUserId(username).map{
         case Some(token) if jwtCoder.decodePrivateToken(token).secret == secret => Some(token)
         case _ => None
      }
   }

}