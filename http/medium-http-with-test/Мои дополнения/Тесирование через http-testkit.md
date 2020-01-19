# Тестирование с akka-http-testkit

Я решил попробовать другие библиотеки тестирования. Здесь я переписал свои зависимости вот так:

```scala
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.6.1",

  "org.scalatest" %% "scalatest" % "3.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.6.1",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.11",

  "org.specs2" %% "specs2-core" % "4.8.3"
)
```

Документация Akka HTTP предлагает не Specs2, но akka-http-testkit.

Получаются вот такие тесты, более привычные после Coursera:

```scala
package com.learning.scala
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import org.scalatest.wordspec.AnyWordSpec
import HealthRoute._
import TestRoute._

class HealthRouteTest extends AnyWordSpec with Matchers with ScalatestRouteTest {
  "http-example" should {

    "return an OK with health request" in {
      // tests:
      Get("/health") ~> healthRoute ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "OK"
      }
    }

    "return a Test with test request" in {
      // tests:
      Get("/test") ~> testRoute ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "TEST"
      }
    }
  }
}

```