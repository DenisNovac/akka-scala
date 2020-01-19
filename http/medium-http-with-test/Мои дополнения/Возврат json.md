# Возврат JSON

Инфраструктура Akka позволяет легко конвертировать объекты в JSON и обратно. Интаграция со `spray-json` доступна из коробки через модуль `akka-http-spray-json`. Для того, чтобы какой-либо роут возвращал JSON - нужно добавить в него поддержку JSON.

Во-первых, нужно описать формат JSON в объектах Scala:

```scala
// модель одного элемента json
final case class Item(id: Long, name: String)
// json может включать много элементов одного вида
final case class Order(items: List[Item])
```

Затем нужно импортировать SprayJsonSupport. Можно сделать это через промежуточный трейт:

```scala
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order)
}
```

Тогда при подключении этого трейта к роуту роут будет получать формат JSON и автоматически поддерживать его:

```scala
object JsonRoute extends Directives with JsonSupport {
  val jsonRoute: Route =
    path("json") {
      get {
        complete(Item(23, "John"))
      }
    }
}

```

Возврат:

```json
{"id":23,"name":"John"}
```

При этом мы можем общаться с JSON-ами в понятиях наших объектов:

```scala
object JsonRoute extends Directives with JsonSupport {
  val jsonRoute: Route =
    path("json") {
      get {
        complete(Order(List(Item(23,"John"), Item(43, "Sam"))))
      }
    }
}

```

Возврат:

```json
{"items":[{"id":23,"name":"John"},{"id":43,"name":"Sam"}]}
```