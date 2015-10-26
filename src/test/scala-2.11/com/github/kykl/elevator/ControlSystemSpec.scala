package com.github.kykl.elevator

/**
 * Created by kykl on 10/25/15.
 */

import org.scalatest._

// InformerControlSystemFactory creates a instance of ControlSystemImpl that uses an Informer to print out internals
// of the system. It uses mixin to blend logging and the system together
object InformerControlSystemFactory {
  def apply(informer:Informer, numberOfElevators:Int, maxFloor:Int):ControlSystem = {
    new ControlSystemImpl(numberOfElevators, maxFloor) with LoggedControlSystem {
      def info(msg:String):Unit = informer(msg)
      def debug(msg:String):Unit = informer(msg)
      def trace(msg:String):Unit = informer(msg)
      def warn(msg:String):Unit = informer(msg)
    }
  }
}
class ControlSystemSpec extends FlatSpec {
  private def createControlSystem(n:Int, maxFloor:Int = 100):ControlSystem = {
    val control = InformerControlSystemFactory(info, n, maxFloor)
    // Not really using the status. If we turned on the DEBUG logging, we should see a log event
    control.status()
    control
  }

  var maxFloor = 10
  val numberOfElevators = 17

  s"$numberOfElevators elevators" should "throw IllegalArgumentException" in {
    intercept[IllegalArgumentException] {
      createControlSystem(numberOfElevators, maxFloor)
    }
  }

  var floor = 11
  s"Pickup at Floor $floor with highest floor $maxFloor" should "throw FloorNotExistException" in {
    val control = createControlSystem(1, maxFloor)
    intercept[FloorNotExistException] {
      control.pickup(floor, Direction.Down)
    }
  }

  var direction = -2
  s"Pickup with wrong direction: $direction" should "throw WrongDirectionException" in {
    val control = createControlSystem(1, maxFloor)

    intercept[WrongDirectionException] {
      control.pickup(4, -3)
    }
    direction = 2
    intercept[WrongDirectionException] {
      control.pickup(4, direction)
    }
  }

  "Status" should "be unchanged after stepping with no pending pickup" in {
    val control = createControlSystem(4, maxFloor)
    val status = control.status()
    control.step()
    val currentStatus = control.status()

    assert(status == currentStatus)
  }

  {
    var expectedResult = "Elevator 1 is at Floor 4; Elevator 2 is at Floor 1"
    "Expected status string" should s"be $expectedResult" in {
      val control = createControlSystem(2, maxFloor)
      var status = control.status()
      control.step()
      val currentStatus = control.status()

      // We should end up with 'Elevator 1 is at Floor 4; Elevator 2 is at Floor 1 going to stop: 1'
      control.status()

      control.pickup(3, Direction.Up)
      control.pickup(4, Direction.Up)

      (1 to 4).foreach { _ =>
        control.step()
        control.status()

      }

      control.pickup(2, Direction.Up)

      (1 to 3).foreach { _ =>
        control.step()
        control.status()

      }

      control.pickup(1, Direction.Up)
      (1 to 2).foreach { _ =>
        control.step()
        status = control.status()
      }

      assert(statusString(status) == expectedResult)
    }
  }

  {
    val maxFloor = 3
    val steps = maxFloor + 1
    val expectedResult = "Elevator 1 is at Floor 3; Elevator 2 is at Floor 1"
    "Elevator" should s"stay at top floor $maxFloor even when step $steps times" in {
      val control = createControlSystem(2, maxFloor)
      var status = control.status()
      control.pickup(3, Direction.Up)

      (1 to steps).foreach { _ =>
        control.step()
        status = control.status()
      }

      info("Line 1 -> " + statusString(status))
      info("Line 2 -> " + expectedResult)
      assert(statusString(status) == expectedResult)
    }
  }


  private def statusString(status:Seq[Status]):String = status.mkString("; ")
}
