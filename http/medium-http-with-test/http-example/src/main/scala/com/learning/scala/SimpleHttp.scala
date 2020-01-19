package com.learning.scala

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.Directives._

object SimpleHttp extends App {
  implicit val system: ActorSystem = ActorSystem("simple-http")
  implicit val log: LoggingAdapter = Logging(system, "main")

  val port = 8080
  val int = "localhost"
  val route = HealthRoute.healthRoute ~ TestRoute.testRoute

  // Роут для выбрасывания 404 в любой непонятной сиутации
  implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder
    .handleNotFound {
      complete(
        (StatusCodes.NotFound,
        "404 not found")
      )
    }
    .result()

  val clientRouteLogged = DebuggingDirectives.logRequestResult(("Client ReST", Logging.InfoLevel))(route)

  val bindingFuture =
    Http().bindAndHandle(clientRouteLogged, int, port)

  log.info(s"Server started at the port $port")
}
