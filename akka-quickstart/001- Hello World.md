# Hello World

Akka - это набор инструментов и рантайм для построения конкурентных, распределённых, отказоустойчивых событийно-ориентированных приложений в JVM.

Из https://github.com/anton-k/ru-neophyte-guide-to-scala/blob/master/src/p14-actors.md :

*Модель акторов — это другой основополагающий подход к распараллеливанию программ. В этом подходе одновременно выполняющиеся процессы передают друг другу сообщения.
Идея акторов не нова. Наиболее выдающаяся реализация была сделана в языке Erlang. В стандартной поставке Scala также есть своя библиотека для реализации акторов, но она устарела и с версии 2.11 будет окончательно заменена на реализацию акторов из библиотеки Akka. Эта библиотека по-сути уже давно является стандартом.*

Акторы - это юниты выполнения в Akka. Они облегчают написание правильных конкурентных, параллельных и распределённых систем. Этот пример Hello World покажет основы Akka.


## Исходный код

Пример можно установить [из Lightbend Tech Hub](https://developer.lightbend.com/start/?group=akka&project=akka-quickstart-scala), нажав кнопку `CREATE A PROJECT FOR ME`.

Для запуска Hello World можно воспользоваться сборщиком sbt, просто запустив его из загруженного каталога:

```bash
[denis@denis-pc akka-quickstart-scala]$ ./sbt

Getting org.scala-sbt sbt 1.1.6 ...
downloading https://repo1.maven.org/maven2/org/scala-sbt/sbt/1.1.6/sbt-1.1.6.jar ...
	[SUCCESSFUL ] org.scala-sbt#sbt;1.1.6!sbt.jar (263ms)
downloading https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.6/scala-library-2.12.6.jar 

.....


[info] Done updating.
[info] Loading settings from build.sbt ...
[info] Set current project to akka-quickstart-scala (in build file:/home/denis/%D0%94%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D1%8B/develop/akka-scala/akka-quickstart/akka-quickstart-scala/)
[info] sbt server started at local:///home/denis/.sbt/1.0/server/5358800e4b4d194c5546/sock
sbt:akka-quickstart-scala> 

```

Теперь для запуска можно ввести `reStart`. Появится примерно такое сообщение:

```
[success] Total time: 21 s, completed 12.01.2020 15:12:57
sbt:akka-quickstart-scala> akka-quickstart-scala[ERROR] SLF4J: A number (1) of logging calls during the initialization phase have been intercepted and are
akka-quickstart-scala[ERROR] SLF4J: now being replayed. These are subject to the filtering rules of the underlying logging system.
akka-quickstart-scala[ERROR] SLF4J: See also http://www.slf4j.org/codes.html#replay
akka-quickstart-scala [2020-01-12 15:12:58,353] [INFO] [akka.event.slf4j.Slf4jLogger] [AkkaQuickStart-akka.actor.default-dispatcher-3] [] - Slf4jLogger started
akka-quickstart-scala [2020-01-12 15:12:58,383] [INFO] [com.example.Greeter$] [AkkaQuickStart-akka.actor.default-dispatcher-5] [akka://AkkaQuickStart/user/greeter] - Hello Charles!
akka-quickstart-scala [2020-01-12 15:12:58,385] [INFO] [com.example.GreeterBot$] [AkkaQuickStart-akka.actor.default-dispatcher-3] [akka://AkkaQuickStart/user/Charles] - Greeting 1 for Charles
akka-quickstart-scala [2020-01-12 15:12:58,385] [INFO] [com.example.Greeter$] [AkkaQuickStart-akka.actor.default-dispatcher-5] [akka://AkkaQuickStart/user/greeter] - Hello Charles!
akka-quickstart-scala [2020-01-12 15:12:58,385] [INFO] [com.example.GreeterBot$] [AkkaQuickStart-akka.actor.default-dispatcher-3] [akka://AkkaQuickStart/user/Charles] - Greeting 2 for Charles
akka-quickstart-scala [2020-01-12 15:12:58,385] [INFO] [com.example.Greeter$] [AkkaQuickStart-akka.actor.default-dispatcher-5] [akka://AkkaQuickStart/user/greeter] - Hello Charles!
akka-quickstart-scala [2020-01-12 15:12:58,385] [INFO] [com.example.GreeterBot$] [AkkaQuickStart-akka.actor.default-dispatcher-3] [akka://AkkaQuickStart/user/Charles] - Greeting 3 for Charles
```


## Что программа делает

Пример состоит из трёх акторов:

- Greet: Получает команды чтобы `Greet` (приветствовать) кого-либо и отвечает с `Greeted`, чтобы подтвердить приветствие;
- GreeterBot: Получает ответ от Greeter и высылает количество принятых приветствий, а также собирает ответы пока не превысит некоторое количество;
- GreeterMain: guardian актор, запускающий отсальное.


## Преимущества Акторов

- Событийно-ориентированная модель - Акторы выполняют работу в ответ на сообщения. Коммуникация между акторами асинхронна, поэтому они отправляют сообщения и продолжают работу без блокирования для ожидания ответа;
- Сильная изоляция - в отличие от обычных объектов Scala, Актор не имеет публичного API (в плане методов, которые можно вызвать). Вместо этого, их API определено через сообщения, которые актор обрабатывает. Это защищает от разделения состояния между акторами, единственный путь проверить другой актор - это послать ему сообщение с просьбой об этом;
- Прозрачность локации - система создаёт акторы из фабрики и возвращает ссылки на экземпляры. Т.к. локация ничего не значит, Акторы могут начинать работу, заканчивать, перемещаться и рестартовать при этом меняя положение;
- Легковесность - каждый инстанс потребляет несколько сотен байт, что позволяет одному приложению иметь миллионы конкурентных Акторов.