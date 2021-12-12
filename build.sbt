ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.1"
ThisBuild / organization := "com.lalafo"
ThisBuild / organizationName := "lalafo"

val SttpVersion = "2.2.10"
val CirceVersion = "0.14.1"
val CSVVersion = "0.6.2"
val AkkaVersion = "2.6.17"

lazy val root = (project in file("."))
  .settings(
    name := "LalafoScrapper",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "com.softwaremill.sttp.client" %% "core" % SttpVersion,
      "com.softwaremill.sttp.client" %% "async-http-client-backend-future" % SttpVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "com.nrinaudo" %% "kantan.csv" % CSVVersion,
      "com.nrinaudo" %% "kantan.csv-generic" % CSVVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion
    )
  )
