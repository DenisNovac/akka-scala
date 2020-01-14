# Главный класс

```scala
object AkkaQuickstart extends App {
  //#actor-system
  val greeterMain: ActorSystem[GreeterMain.Start] = ActorSystem(GreeterMain(), "AkkaQuickStart")
  //#actor-system

  //#main-send-messages
  greeterMain ! Start("Charles")
  //#main-send-messages
}
```

Объект `AkkaQuickstart` создаёт `ActorSystem` вместе с актором-хранителем и именем. Актор-хранитель - это актор верхнего уровня, разворачивающий приложение. Хранитель обычно содержит поведение `Behaviors.setup`. Т.к. оно определено в apply - получается, что в ActorSystem первым аргументом передаётся само поведение GreeterMain (apply сводит вызов функции к простому написанию GreeterMain()).

```scala
object GreeterMain {

  final case class Start(name: String)

  def apply(): Behavior[Start] =
    Behaviors.setup { context =>
      //#create-actors
      val greeter = context.spawn(Greeter(), "greeter")
      //#create-actors

      Behaviors.receiveMessage { message =>
        //#create-actors
        val replyTo = context.spawn(GreeterBot(max = 3), message.name)
        //#create-actors
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same
      }
    }
}
```