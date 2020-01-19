name := "http-example"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.6.1",

  "org.scalatest" %% "scalatest" % "3.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.6.1",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.11",

  "org.specs2" %% "specs2-core" % "4.8.3"
)