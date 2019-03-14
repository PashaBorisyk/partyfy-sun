package dao

import dao.sql.{Sql, UserRegistrationSql, UserSql}
import dao.traits.UserRegistrationDAO
import javax.inject.Inject
import models.persistient.{User, UserRegistration, UserRegistrationState, UserState}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserRegistrationDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with UserRegistrationDAO[Future] {

   override def createUserRegistration(userRegistration: UserRegistration) = {

      val query =
         UserSql.checkUserExistence(userRegistration.username)
            .zip(UserRegistrationSql.existsWithRegistrationToken(userRegistration.registrationToken))
            .flatMap { userExistsWithRegistrationExists =>

               val (userExist, registrationExists) = userExistsWithRegistrationExists
               if (!userExist && !registrationExists) {
                  UserSql.create(User(username = userRegistration.username)).andThen(UserRegistrationSql.create
                  (userRegistration))
               } else if (userExist && !registrationExists) {
                  Sql(userRegistration.copy(state = UserRegistrationState.DUPLICATE))
               } else if (userExist && registrationExists) {
                  Sql(userRegistration)
               } else {
                  throw new RuntimeException("Illegal situation : user registration found, but no corresponding user " +
                     "in database.")
               }

            }
      db.run(query)
   }

   override def finishRegistrationAndGetUser(userRegistration: UserRegistration, tokenGen: Long => String) = {
      val query = UserRegistrationSql
         .findByRegistrationToken(userRegistration.registrationToken)
         .zip(UserSql.getByUsername(userRegistration.username)).flatMap {

         case (Some(registration), Some(user)) =>

            if(registration.expirationDateMills < System.currentTimeMillis())
               UserRegistrationSql.update(registration.copy(state = UserRegistrationState.EXPIRED)).zip(Sql(user))

            val newRegisteredUser = user.copy(token = tokenGen(user.id), state = UserState.ACTIVE)
            UserRegistrationSql
               .update(registration.copy(state = UserRegistrationState.CONFIRMED))
               .zip(UserSql.updateUser(newRegisteredUser))

         case _ => throw new RuntimeException("Cannot find user or user registration by given token")

      }
      db.run(query)
   }

   def deleteUserRegistration(registrationId: Long) = {
      db.run(UserRegistrationSql.deleteUserRegistration(registrationId))
   }

}