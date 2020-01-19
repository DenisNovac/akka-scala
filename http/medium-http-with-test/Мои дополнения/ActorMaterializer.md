# ActorMaterializer is deprecated

```scala
implicit val materializer: ActorMaterializer = ActorMaterializer()

Symbol ActorMaterializer is deprecated. The Materializer now has all methods the ActorMaterializer used to have 

ActorMaterializer is deprecated. Use the system wide materializer with stream attributes or configuration settings to change defaults 
```

Начиная с Akka 2.6. использование ActorMaterializer, как выше, уже не требуется. Более того, этот кусок кода вообще можно убрать, ведь ActorMaterializer неявно создаётся в ActorSystem:

```scala
  /**
   * Implicitly provides the system wide materializer from a classic or typed `ActorSystem`
   */
  implicit def matFromSystem(implicit provider: ClassicActorSystemProvider): Materializer =
    SystemMaterializer(provider.classicSystem).materializer
```

Таким образом, код запуска сервиса будет ещё проще:

```scala
object SimpleHttp extends App {
  implicit val system: ActorSystem = ActorSystem("simple-http")

  implicit val log: LoggingAdapter = Logging(system, "main")

  val port = 8080
  val int = "localhost"
  val route = HealthRoute.healthRoute

  val bindingFuture =
    Http().bindAndHandle(route, int, port)

  log.info(s"Server started at the port $port")
}
```