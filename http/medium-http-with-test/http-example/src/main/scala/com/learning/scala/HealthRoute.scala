package com.learning.scala
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{PathMatcher, Route}

object HealthRoute {
  val healthRoute: Route =
    path("health") {
      get {
        complete(StatusCodes.OK)
      }
    }

}
