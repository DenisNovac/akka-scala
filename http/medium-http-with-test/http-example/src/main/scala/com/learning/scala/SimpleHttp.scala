package com.learning.scala

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer


object SimpleHttp extends App {
  implicit val system: ActorSystem = ActorSystem("simple-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val log: LoggingAdapter = Logging(system, "main")
  val port = 8080
  val int = "localhost"

  val route = HealthRoute.healthRoute


  val bindingFuture =
    Http().bindAndHandle(route, int, port)

  log.info(s"Server started at the port $port")
}
