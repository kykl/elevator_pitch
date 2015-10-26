package com.github.kykl.elevator

import org.slf4j.LoggerFactory

/**
 * Created by kykl on 10/25/15.
 */
object App {
  def main(args: Array[String]): Unit = {
    // Get a logged controlled system
    val control:ControlSystem = ControlSystemFactory(LoggerFactory.getLogger(classOf[LoggedControlSystem]), 2, 10)

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
      control.status()
    }
  }
}
