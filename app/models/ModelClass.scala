package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

//
//case class Users(userId:Int,first:String,last:String)
//
//class UserDAO(tag:Tag) extends Table[Users](tag,"USER"){
//
//   def userId = column[Int]("ID",O.PrimaryKey,O.AutoInc)
//   def first = column[String]("FIRST")
//   def last = column[String]("LAST")
//
//   def * = (userId,first,last) <> (Users.tupled,Users.unapply)
//
//}
//
//class MyModelClass(tag:Tag) extends Table[(Long,String)](tag,"COFFEES"){
//
//   def userId = column[Long]("ID",O.PrimaryKey,O.AutoInc)
//   def name = column[String]("NAME")
//
//   override def * = (userId,name)
//
//}