package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers

abstract class BaseTestSuite extends PlaySpec with GuiceOneServerPerSuite {

//   final val inMemoryDatabaseConf = Map(
//      "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
//      "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
//      "slick.dbs.default.db.driver" -> "org.h2.Driver",
//      "slick.dbs.default.db.url" -> "jdbc:h2:mem:play",
//      "slick.dbs.default.db.keepAliveConnection" -> "true",
//      "slick.dbs.default.db.user" -> "pashaborisyk",
//      "slick.dbs.default.db.password" -> "Puschinarij1"
//   )

   override def fakeApplication() = new GuiceApplicationBuilder().configure(Helpers.inMemoryDatabase()).build()

}
