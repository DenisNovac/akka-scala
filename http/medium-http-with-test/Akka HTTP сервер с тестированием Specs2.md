# Базовый пример Akka HTTP с тестированием

В этот раз напишем зависимости в `build.sbt:

```scala
libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.1"
```


## Endpoint

Добавляем первый `Endpoint` в приложение в файл `HealthRoute.scala`:

```scala
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
```

**Route** - основа обработки запросов HTTP в Akka. Это тип-псевдоним (*type alias*), определённый как `type Route = RequestContext => Future[RouteResult]`. Из определения видно, что Route - это функция, которая превращает запрос (контекст) в ответ. `Future` говорит, что прверащение должно быть асинхронным.

Метод `path` в качестве параметра принимает `PathMatcher`, который будет использоваться сервером чтобы найти обработчик для запрошенного URL. Этот красивый синтаксис строится на сахаре Scala - возможности опустить `apply` и возможности не писать скобки за счёт инфиксной нотации. В обычной записи это выглядело бы так:

```scala
val healthRoute: Route = path(PathMatcher.apply("health")).apply(get.apply(complete(StatusCodes.OK)))
```

За счёт apply это сократится так:

```scala
val healthRoute: Route = path(PathMatcher("health"))(get(complete(StatusCodes.OK)))
```

Этот роут возвращает OK на запрос health. Теперь напишем для него тест с использованием `Specs2`.


## Тестирование со Specs2

Дополнительные зависимости:

```scala
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "latest.integration"
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "latest.integration"
libraryDependencies += "org.specs2" %% "specs2-core" % "latest.integration"
```

Текст теста:

```scala
package com.learning.scala

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.Specs2RouteTest
import com.learning.scala.HealthRoute.healthRoute
import org.specs2.Specification
import org.specs2.specification.core.SpecStructure

class HealthRouteSpec extends Specification with Specs2RouteTest {
  override def is: SpecStructure =
    s2"""
        Health Check route should
          respond HTTP 200 $e1
          have OK text message as response $e2
      """

  private def e1 =
    Get("/health") ~> healthRoute ~> check {
      status must beEqualTo(StatusCodes.OK)
    }

  private def e2 =
    Get("/health") ~> healthRoute ~> check ({
      responseAs[String] must beEqualTo("OK")
    })
}
```

- Метод is определяет структуру теста на основе библиотеки Specs2, `e1` и `e2` - это сам тест. Синтаксис специально заточен под написание тестов в формате спецификаций.
- Метод `Get` создаёот объект http-запроса.
  - Первый метод `~>` из интерфейса RouteTest приминяет этот запрос к указанному Route.
    - Route - это по сути функция, так что мы просто вызываем функцию с созданным HTTP запросом в качестве параметра.
  - Второй метод `~>` из интерфейса `RouteTestResult` получает в качестве параметра функцию `RouteTestResult => T` . Она преобразует RouteTestResult в `MatchResult[T]`.
  - `MatchResult[T]` - это то, что необходимо Specs2, чтобы определить, завершилось ли выполнение теста. Эта функция-параметр возвращается вызовом check {}.

Эти тесты можно сразу запускать, даже без самого сервера.


## Конфигурация логгера

Создадим файл `src/main/resources/application.conf`, в котором будет храниться конфигурация логгера.

```scala
akka {
    loggers = ["akka.event.Logging$DefaultLogger"]
    loglevel = "INFO"
}
```

## Инициализация приложения

Затем создадим главный класс приложения:

```scala
package com.learning.scala

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object SimpleHttp extends App {
  implicit val system: ActorSystem = ActorSystem("simple-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val log = Logging(system, "main")
  val port = 8080

  val bindingFuture =
    Http().bindAndHandle(HealthRoute.healthRoute, "localhost", port)

  log.info(s"Server started at the port $port")
}
```

Построчно:

```scala
implicit val system: ActorSystem = ActorSystem("simple-http")
implicit val materializer: ActorMaterializer = ActorMaterializer()
```

Это инициализация системы акторов(Akka Actors), а затем системы материализации акторов (Akka Streams). На них строится библиотека Akka HTTP.


```scala
val bindingFuture =
  Http().bindAndHandle(HealthRoute.healthRoute, "localhost", port)  
```

Это инициалиация HTTP-сервера, в который мы передаём наш роут, чтобы запросы обрабатывались так, как мы запрограммировали


Запуск сервера с проверкой:

```bash
curl http://localhost:8080/health
OK
```


**Источник**:

https://medium.com/@caffeine.notes/akka-http-%D1%81%D0%B5%D1%80%D0%B2%D0%B5%D1%80-%D0%BD%D0%B0-scala-%D0%B1%D0%B0%D0%B7%D0%BE%D0%B2%D1%8B%D0%B9-%D0%BF%D1%80%D0%B8%D0%BC%D0%B5%D1%80-%D0%B2%D0%BA%D0%BB%D1%8E%D1%87%D0%B0%D1%8F-%D1%82%D0%B5%D1%81%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-7eb740099e63