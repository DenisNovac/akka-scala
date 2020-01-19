# Обработка Reject-ов

Но как протестировать то, что несуществующие запросы отбрасываются?

```scala
"return NotFound with random request" in {
  Get("/sdfsdfsdfsf") ~> testRoute ~> check {
    responseAs[String] shouldEqual "Request was rejected"
  }
}
```

Вот так я получаю исключение:

```scala
Request was rejected
ScalaTestFailureLocation: com.learning.scala.HealthRouteTest at (HealthRouteTest.scala:32)
org.scalatest.exceptions.TestFailedException: Request was rejected
```

Сначала нужно обрабатывать несуществующие запросы. 


## Rejections

Директива `~` позволяет соединять роуты при передаче в хендлер в нужном порядке. Если предыдущий роут сделал reject - проверяется match для следующего path. Reject это альтернатива выбрасыванию ошибки. Простое выбрасывание ошибки сделало бы невозможным связывание нескольких роутов. 

После получения Reject от роута запрос продолжит продвигаться через структуру роутов и, возможно, найдёт подходящий. Если reject-ов на пути много - они все запоминаются. 

Если запрос не может быть обработан структурой роутов - закрывающая `handleRejections` директива может быть вызвана, чтобы конвертировать все собранные reject-ы в один HttpResponse, обычно это ошибка 404. `Route.seal()`, например, внутри работает через эту директиву.


## RejectionHandler

Директива `handleRejections` делегирует работу над списком reject-ов переданному `RejectionHandler`. Дефолтный `RejectionHandler` применяется на самом верхнем уровне кода, который превращает `Route` в `Flow`. 

`RejectionHandler` можно передавать неявно в HTTP Handler:

```scala
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


  val clientRouteLogged = DebuggingDirectives.logRequestResult("Client ReST", Logging.InfoLevel)(route)

  val bindingFuture =
    Http().bindAndHandle(clientRouteLogged, int, port)

  log.info(s"Server started at the port $port")
}
```

Кстати говоря, сначала я случайно импортировал `RejectionHandler` для Java и получил отсутствие метода `result()` в `RejectionHandler` и ошибку Type mismatch required: Route, Found: StandardRoute.

Теперь все неизвестные роуты возвращают "404 not found".

Однако, в тестах я все ещё получал **Request was rejected**. 


## Sealed Routes

Дело в том, что RoutingSpec (то, что производит тестирование в библиотеке) возвращает сам факт Reject-а, а не HTTP Response для него. Для того, чтобы вызвать сам Response, нужно:

```scala
"404" in {
  // tests:
  Get("/dsfdfsd") ~> Route.seal(testRoute) ~> check {
    status.intValue() shouldBe 404
  }
}
```

Однако, и тут не используется сам написанный хендлер, здесь прорабатывается лишь ошибка 404, отправляемая по дефолту Akka Http:

```scala
"404" in {
  // tests:
  Get("/dsfdfsd") ~> Route.seal(testRoute) ~> check {
    status.intValue() shouldBe 404
    responseAs[String] shouldEqual "404 not found"
  }
}
```

Возвращает: `"[The requested resource could not be found.]" did not equal "[404 not found]"`

