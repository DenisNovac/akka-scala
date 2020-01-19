package com.learning.scala
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object HealthRoute {
  val healthRoute: Route =
    path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }
}

object TestRoute {

  val testRoute: Route =
    path("test") {
      get {
        complete("TEST")
      }
    }
}