import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

object FlowStyle extends App {
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()

  def flow: Flow[Message, Message, Any] = {
    Flow[Message].collect {
      case TextMessage.Strict(t) => t
    }.map { text =>
      TextMessage.Strict(s"echo: $text")
    }
  }

  val route = path("ws")(handleWebSocketMessages(flow))
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()

  import system.dispatcher
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}