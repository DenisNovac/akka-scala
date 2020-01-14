# Создание акторов

Мы уже видели на описание акторов и их сообщений. Теперь изучим создание акторов.


## Мощь прозрачности местоположения

В Akka **нельзя** создавать инстансы Акторов используя `new`. Вместо этого, акторы создаются через фабричный метод `spawn`. Он не возвращает сам объект, но только его ссылку типа `akka.actor.typed.ActorRef`, которая указывает на инстанс актора. Этот уровень индирекции добавляет много возможностей и гибкости.

В Akka **местоположение не имеет значения**. Прозрачности местоположения означает, что `ActorRef` может отображать инстанс работающего актора здесь или даже на удалённой машине (пока сохраняется общая семантика).

Если нужно, рантайм может оптимизировать систему, изменяя локацию Актора или топологию всего приложения во время работы. Это реализация модели менеджмента ошибок "let it crash", в которой система может самостоятельно восстанавливаться, убивая сломанные акторы и запуская рабочие.


## The Akka ActorSystem

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

**ActorSystem** - это точка входа в приложение. Обычно она одна на приложение. ActorSystem имеет имя и актор-хранитель (guardian actor). Бутстрапинг самого приложения обычно происходит через актор-хранитель. Для нашего приложения актор-хранитель это GreeterMain:

```scala
val greeterMain: ActorSystem[GreeterMain.Start] = ActorSystem(GreeterMain(), "AkkaQuickStart")
```

Он использует `Behaviors.setup` для запуска приложения:

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


## Spawning child actors

Другие акторы создаются через метод `spawn` объекта `ActorContext`. `GreeterMain` создаёт актор `Greeter` на старте программы в поведении `Behaviors.setup`. Затем это поведение возвращает новое поведение - `Behaviors.receiveMessage`. Оно запускается (и создаёт GreeterBot) при каждом получении сообщения-экземпляра класса `Start`.