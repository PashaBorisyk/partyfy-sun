package dao.sql.tables

import slick.jdbc.PostgresProfile.api._
import implicits._
import models.persistient.{User, UserSex, UserState}

private[sql] class UserTable(tag: Tag) extends Table[User](tag, "user") {

   def id = column[Int]("id",O.PrimaryKey, O.AutoInc)

   def username = column[String]("username", O.Unique)

   def token = column[String]("token", O.Unique)

   def name = column[String]("name")

   def surname = column[String]("surname")

   def sex = column[UserSex]("sex")

   def isOnline = column[Boolean]("is_online")

   def status = column[String]("status")

   def latitude = column[Double]("latitude")

   def longtitude = column[Double]("longtitude")

   def imageId = column[Long]("image_id")

   def email = column[String]("email",O.Unique)

   def state = column[UserState]("state")

   def * = (id, username,
      token, name,
      surname, sex,
      isOnline,
      status,
      latitude, longtitude,
      imageId,email,state) <> (User.tupled, User.unapply)

}
