package dao.sql.tables

import models.persistient.{UserRegistrationState, UserSex, UserState}
import slick.ast.TypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._

package object implicits {

   implicit val userRegistrationStateMapper: JdbcType[UserRegistrationState] with TypedType[UserRegistrationState] =
      MappedColumnType.base[UserRegistrationState, String](
         enumState => enumState.toString,
         stringState => UserRegistrationState.valueOf(stringState)
      )

   implicit val userStateMapper: JdbcType[UserState] with TypedType[UserState] =
      MappedColumnType.base[UserState, String](
         enumState => enumState.toString,
         stringState => UserState.valueOf(stringState)
      )

   implicit val userSexMapper: JdbcType[UserSex] with TypedType[UserSex] =
      MappedColumnType.base[UserSex, String](
         enumState => enumState.toString,
         stringState => UserSex.valueOf(stringState)
      )

}
