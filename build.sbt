name := "MyScalaApp"
 
version := "1.0" 
      
lazy val `myscalaapp` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
   ehcache ,
   ws , specs2 % Test , guice,
   "org.postgresql" % "postgresql" % "42.1.4.jre7",
   "com.typesafe.slick" %% "slick" % "3.2.1",
   "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
   "com.typesafe.play" %% "play-slick" % "3.0.0",
   "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
   "com.google.code.gson" % "gson" % "2.8.2",
   "org.imgscalr" % "imgscalr-lib" % "4.2",
   "com.pauldijou" %% "jwt-play" % "0.16.0"
)

libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"



unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )