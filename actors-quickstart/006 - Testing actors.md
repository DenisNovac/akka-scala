# Тестирование акторов

Тест из проекта иллюстрирует использование фреймворка `ScalaTest`:

```scala
//#full-example
package com.example

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.example.Greeter.Greet
import com.example.Greeter.Greeted
import org.scalatest.WordSpecLike

//#definition
class AkkaQuickstartSpec extends ScalaTestWithActorTestKit with WordSpecLike {
//#definition

  "A Greeter" must {
    //#test
    "reply to greeted" in {
      val replyProbe = createTestProbe[Greeted]()
      val underTest = spawn(Greeter())
      underTest ! Greet("Santa", replyProbe.ref)
      replyProbe.expectMessage(Greeted("Santa", underTest.ref))
    }
    //#test
  }

}
//#full-example

```

Этот код не заверщён, но показывает базовые концепты.


## Определение тестового класса

Расширение `ScalaTestWithActorTestKit` позволяет использовать фреймворк `ScalaTest`. Akka поддерживает и другие фреймворки.


## Тестовые методы

Тест использует `TestProbe` для опроса и подтверждения правильности работы поведения:

```scala
"reply to greeted" in {
  val replyProbe = createTestProbe[Greeted]()
  val underTest = spawn(Greeter())
  underTest ! Greet("Santa", replyProbe.ref)
  replyProbe.expectMessage(Greeted("Santa", underTest.ref))
}
```


Я написал такой тест

```scala
"GreeterBot" must {
  "be stopped after 3 times" in {
    val max = 3
    val underTest = spawn(GreeterBot(max))
    val greeter = spawn(Greeter())

    greeter ! Greet("Santa23", underTest.ref)
    greeter ! Greet("Santa24", underTest.ref)
    greeter ! Greet("Santa25", underTest.ref)
    greeter ! Greet("Santa26", underTest.ref)
    greeter ! Greet("Santa26", underTest.ref)  // этого действительно не происходит, но как проверить?
  }
}
```

```scala
[2020-01-12 23:14:30,640] [INFO] [com.example.Greeter$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-7] [akka://AkkaQuickstartSpec/user/$b] - Hello Santa23!
[2020-01-12 23:14:30,641] [INFO] [com.example.Greeter$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-7] [akka://AkkaQuickstartSpec/user/$b] - Hello Santa24!
[2020-01-12 23:14:30,641] [INFO] [com.example.GreeterBot$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-3] [akka://AkkaQuickstartSpec/user/$a] - Greeting 1 for Santa23
[2020-01-12 23:14:30,642] [INFO] [com.example.Greeter$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-7] [akka://AkkaQuickstartSpec/user/$b] - Hello Santa25!
[2020-01-12 23:14:30,642] [INFO] [com.example.GreeterBot$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-3] [akka://AkkaQuickstartSpec/user/$a] - Greeting 2 for Santa24
[2020-01-12 23:14:30,642] [INFO] [com.example.Greeter$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-7] [akka://AkkaQuickstartSpec/user/$b] - Hello Santa26!
[2020-01-12 23:14:30,642] [INFO] [com.example.GreeterBot$] [AkkaQuickstartSpec-akka.actor.default-dispatcher-3] [akka://AkkaQuickstartSpec/user/$a] - Greeting 3 for Santa25
```

Я не уверен, как проверить, стопнуто ли поведение. Возможно, в другом фреймворке?