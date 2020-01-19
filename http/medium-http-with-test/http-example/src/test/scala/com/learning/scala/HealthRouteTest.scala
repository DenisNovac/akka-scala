package com.learning.scala
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import HealthRoute._
import TestRoute._
import akka.http.scaladsl.server.Route

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

    "404" in {
      // tests:
      Get("/dsfdfsd") ~> Route.seal(testRoute) ~> check {
        status.intValue() shouldBe 404
        //responseAs[String] shouldEqual "404 not found"
      }
    }

  }
}
