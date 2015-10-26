package com.github.kykl.elevator

/**
 * Created by kykl on 10/25/25.
 */

trait ControlSystem {
  def status(): Seq[Status]

  def update(status:Status)

  @throws(classOf[FloorNotExistException])
  @throws(classOf[WrongDirectionException])
  def pickup(fromFloor:Int, direction:Int)

  def step()
}

case class WrongDirectionException(direction:Int) extends Exception(s"Direction $direction does not exist")

case class FloorNotExistException(floorNumber:Int) extends Exception(s"Floor $floorNumber does not exist")

