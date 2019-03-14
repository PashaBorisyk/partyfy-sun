package dao.sql.tables

import slick.jdbc.PostgresProfile.api._
import implicits._
import models.persistient.{User, UserState}

private[sql] class UserTable(tag: Tag) extends Table[User](tag, "user") {

   def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)

   def username = column[String]("username", O.Unique)

   def token = column[String]("token")

   def name = column[String]("name")

   def surname = column[String]("surname")

   def isMale = column[Boolean]("is_male")

   def isOnline = column[Boolean]("is_online")

   def status = column[String]("status")

   def latitude = column[Double]("latitude")

   def longtitude = column[Double]("longtitude")

   def imageId = column[Long]("image_id",O.Unique)

   def email = column[String]("email",O.Unique)

   def state = column[UserState]("state")

   def * = (id, username,
      token, name,
      surname, isMale,
      isOnline,
      status,
      latitude, longtitude,
      imageId,email,state) <> (User.tupled, User.unapply)

}