package com.learning.scala

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.{Directives, Route}
import spray.json.DefaultJsonProtocol

// модель одного элемента json
final case class Item(id: Long, name: String)
// json может включать много элементов одного вида
final case class Order(items: List[Item])

// при подключении этого трейта роут станет поддерживать JSON:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order)
}

object JsonRoute extends Directives with JsonSupport {
  val jsonRoute: Route =
    path("json") {
      get {
        complete(Order(List(Item(23,"John"), Item(43, "Sam"))))
      }
    }
}
