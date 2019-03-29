package dao.sql.tables

import models.persistient._
import slick.ast.TypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._

package object implicits {

   implicit val userRegistrationStateMapper
   : JdbcType[UserRegistrationState] with TypedType[UserRegistrationState] =
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

   implicit val usersRelationMapper
   : JdbcType[UsersRelationType] with TypedType[UsersRelationType] =
      MappedColumnType.base[UsersRelationType, String](
         enumState => enumState.toString,
         stringState => UsersRelationType.valueOf(stringState)
      )

   implicit val eventPrivacyTypeMapper
   : JdbcType[EventPrivacyType] with TypedType[EventPrivacyType] =
      MappedColumnType.base[EventPrivacyType, String](
         enumState => enumState.toString,
         stringState => EventPrivacyType.valueOf(stringState)
      )

   implicit val eventStateMapper
   : JdbcType[EventState] with TypedType[EventState] =
      MappedColumnType.base[EventState, String](
         enumState => enumState.toString,
         stringState => EventState.valueOf(stringState)
      )

}
