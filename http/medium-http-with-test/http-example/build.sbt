name := "http-example"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.1"


libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "latest.integration"
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "latest.integration"
libraryDependencies += "org.specs2" %% "specs2-core" % "latest.integration"
