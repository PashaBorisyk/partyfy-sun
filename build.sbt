name := "MyScalaApp"

version := "1.0"

lazy val `myscalaapp` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
   ehcache,
   ws, specs2 % Test, guice,
   "org.postgresql" % "postgresql" % "42.1.4.jre7",
   "com.typesafe.slick" %% "slick" % "3.2.3",
   "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
   "com.typesafe.play" %% "play-slick" % "3.0.0",
   "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
   "com.typesafe.play" %% "play-mailer" % "6.0.1",
   "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
   "com.google.code.gson" % "gson" % "2.8.2",
   "org.imgscalr" % "imgscalr-lib" % "4.2",
   "com.pauldijou" %% "jwt-play" % "0.16.0",
   "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.1",
   "org.apache.kafka" % "kafka-clients" % "2.1.0",
   "org.scalactic" %% "scalactic" % "3.0.5",
   "org.scalatest" %% "scalatest" % "3.0.5" % "test",
   "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % "test",
   "com.h2database" % "h2" % "1.4.192"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

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