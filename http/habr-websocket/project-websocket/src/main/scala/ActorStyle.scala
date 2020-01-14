import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl._
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

object ActorStyle extends App{
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()

  def flow: Flow[Message, Message, Any] = {
    val client = system.actorOf(Props(classOf[ClientConnectionActor]))
    val in = Sink.actorRef(client, "sinkclose")
    val out = Source.actorRef(8, OverflowStrategy.fail).mapMaterializedValue {
      a =>
        client ! ("income" -> a)
        a
    }
    Flow.fromSinkAndSource(in, out)
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



class ClientConnectionActor extends Actor {
  var connection: Option[ActorRef] = None

  val receive: Receive = {
    case ("income", a: ActorRef) ⇒ connection = Some(a); context.watch(a)
    case Terminated(a) if connection.contains(a) => connection = None; context.stop(self)
    case "sinkclose" => context.stop(self)

    case TextMessage.Strict(t) => connection.foreach(_ ! TextMessage.Strict(s"echo $t"))
    case _ => // ignore
  }

  override def postStop(): Unit = connection.foreach(context.stop)
}
