name := "MyScalaApp"

version := "1.0"

lazy val `myscalaapp` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
   ehcache,
   ws, specs2 % Test, guice,
   "org.postgresql" % "postgresql" % "42.1.4.jre7",
   "com.typesafe.slick" %% "slick" % "3.3.0",
   "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0",
   "com.typesafe.play" %% "play-slick" % "4.0.0",
   "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
   "com.typesafe.play" %% "play-mailer" % "7.0.0",
   "com.typesafe.play" %% "play-mailer-guice" % "7.0.0",
   "com.google.code.gson" % "gson" % "2.8.5",
   "org.imgscalr" % "imgscalr-lib" % "4.2",
   "com.pauldijou" %% "jwt-play" % "2.0.0",
   "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
   "org.apache.kafka" % "kafka-clients" % "2.1.1",
   "org.scalactic" %% "scalactic" % "3.0.5",
   "org.scalatest" %% "scalatest" % "3.0.5" % "test",
   "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % "test",
   "com.h2database" % "h2" % "1.4.192",
   "com.ning" % "async-http-client" % "1.9.40",
   "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.1"
)

mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
   case manifest if manifest.contains("MANIFEST.MF") =>
      // We don't need manifest files since sbt-assembly will create
      // one with the given settings
      MergeStrategy.discard
   case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
      // Keep the content for all reference-overrides.conf files
      MergeStrategy.concat
   case x =>
      // For all the other files, use the default sbt-assembly merge strategy
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
}