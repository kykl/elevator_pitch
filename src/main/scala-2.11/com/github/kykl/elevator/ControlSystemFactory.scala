package com.github.kykl.elevator

/**
 * Created by kykl on 10/25/15.
 */

object ControlSystemFactory {
  def apply(numberOfElevators:Int, maxFloor:Int):ControlSystem = new ControlSystemImpl(numberOfElevators, maxFloor)

  def apply(logger:org.slf4j.Logger, numberOfElevators:Int, maxFloor:Int):ControlSystem = {
    new ControlSystemImpl(numberOfElevators, maxFloor) with LoggedControlSystem {
      def info(msg:String):Unit = logger.info(msg)
      def debug(msg:String):Unit = logger.debug(msg)
      def trace(msg:String):Unit = logger.trace(msg)
      def warn(msg:String):Unit = logger.warn(msg)
    }
  }
}

trait Logger {
  def info(msg:String):Unit
  def debug(msg:String):Unit
  def trace(msg:String):Unit
  def warn(msg:String):Unit
}

trait LoggedControlSystem extends ControlSystem with Logger {
  abstract override def status(): Seq[Status] = {
    val status = super.status()
    info("Status: " + status.mkString("; "))

    status
  }

  abstract override def pickup(from: Int, direction: Int): Unit = {
    val dir = if (direction > 0) "UP" else "DOWN"
    info(s"Pickup: request at Floor $from going $dir")
    super.pickup(from, direction)
  }

  abstract override def update(status:Status): Unit = {
    info("Update: " + status.toString())
    super.update(status)
  }

  abstract override def step(): Unit = {
    info("Step")
    super.step()
  }
}
