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
  }
}
//#full-example
