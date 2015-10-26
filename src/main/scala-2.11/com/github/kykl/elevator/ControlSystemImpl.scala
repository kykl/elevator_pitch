package com.github.kykl.elevator

import scala.collection.mutable

/**
 * Created by kykl on 10/25/15.
 */

object ZeroElevator extends Elevator(0)

object Direction {
  val Up = 1
  val Idle = 0
  val Down = -1
}

case class Pickup(floor:Int, direction:Int)

case class Status(id:Int, var currentFloor:Int, var stops:Seq[Int]) {
  override def toString(): String = {
    val idFloor = s"Elevator $id is at Floor ${currentFloor}"
    if (stops.isEmpty)
      return idFloor
    else {
      val stringOfStops = stops.mkString(",")
      val stop = if (stops.length == 1) "stop" else "stops"
      return s"$idFloor going to ${stop}: ${stringOfStops}"
    }
  }

  override def equals(rhs: Any) = {
    rhs match {
      case that: Status => this.id == that.id && this.currentFloor == that.currentFloor && this.stops == that.stops
      case _ => false
    }
  }

  // TODO: override hashCode
}

case class Elevator(id:Int, maxFloor:Int = 101) {
  var direction = Direction.Idle
  var status = Status(id, 1, List[Int]())

  def isIdle = direction == Direction.Idle

  def distance(pickup:Pickup):Int = {
    if (!isIdle && direction != pickup.direction)
      return Int.MaxValue

    math.abs(pickup.floor - status.currentFloor)
  }

  def move():Unit = {
    if (status.stops.isEmpty) {
      direction = Direction.Idle
      return
    }

    val firstStop = status.stops(0)
    // Assuming the stops are sorted
    val newDirection =
      if (direction == Direction.Up && status.currentFloor > firstStop)
        Direction.Down
      else if (direction == Direction.Down && status.currentFloor < firstStop)
        Direction.Up
      else
        direction

    if (newDirection == Direction.Up) {
      if (status.currentFloor == maxFloor) {
        direction = Direction.Down
        status.currentFloor = status.currentFloor - 1
      } else
        status.currentFloor = status.currentFloor + 1
    } else if (newDirection == Direction.Down) {
      if (status.currentFloor == 1) {
        direction = Direction.Up
        status.currentFloor = status.currentFloor + 1
      } else
        status.currentFloor = status.currentFloor - 1
    }
  }
}

class ControlSystemImpl(numOfElevators:Int, maxFloor:Int = 100) extends ControlSystem {
  if (numOfElevators > 16 || numOfElevators < 1) throw new IllegalArgumentException("Number of elevators need to be between 1-16")
  private val elevators = (1 to numOfElevators).map(Elevator(_))
  private val pickupQueue = new mutable.Queue[Pickup]

  override def status(): Seq[Status] = elevators.map(_.status)

  override def update(status:Status): Unit = elevators.map{ e =>
    if (status.id == e.status.id) {
      e.status = status
    }
    e
  }

  override def pickup(from: Int, direction: Int): Unit = {
    if (from < 0 || from > maxFloor)
      throw FloorNotExistException(from)

    if (!(direction == Direction.Up || direction == Direction.Down))
      throw WrongDirectionException(direction)

    pickupQueue.enqueue(Pickup(from, direction))
  }

  override def step(): Unit = {
    val notPickedUp = mutable.Queue[Pickup]()

    while (!pickupQueue.isEmpty) {
      val pickup = pickupQueue.dequeue()
      // look for the closest elevator in the going the same direction or idle
      val chosen = elevators
        .filter { e =>
          e.isIdle || pickup.direction == e.direction
        }
        .foldLeft(ZeroElevator: Elevator)((found, current) => {
          if (found == ZeroElevator) {
            current
          } else {
            val lastDistance = found.distance(pickup)
            val currentDisance = current.distance(pickup)
            if (currentDisance < lastDistance) current else found
          }
        })

      if (chosen != ZeroElevator) {
        val stops = chosen.status.stops :+ pickup.floor
        update(Status(chosen.status.id, chosen.status.currentFloor, stops))
        chosen.direction = pickup.direction
      } else {
        notPickedUp.enqueue(pickup)
      }
    }

    // TODO: doesn't seem to be fair to put pickup back in queue at the end again! We should just put them back to front again!
    if (!notPickedUp.isEmpty)
      notPickedUp.foreach { p => pickupQueue.enqueue(p) }

    elevators.foreach{ e =>
      val numberOfStops = e.status.stops.length
      val stops = e.status.stops.filter{ stop =>
        e.status.currentFloor != stop
      }

      if (numberOfStops != stops.length)
        update(Status(e.status.id, e.status.currentFloor, stops))


      if (e.status.stops.isEmpty) {
        e.direction = Direction.Idle
      } else {
        e.move()
      }
    }
  }
}
