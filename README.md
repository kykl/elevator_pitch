Elevator Control System
=======================
Design and implement an elevator control system in Scala programming language. This elevator control system can handle up to 16 elevators and provide an interface for:

* Querying the state of the elevators (what floor are they on and where they are going),
* receiving an update about the status of an elevator,
* receiving a pickup request,
* time-stepping the simulation.

For example, we could imagine in Scala an interface like this:

```
trait ControlSystem {
  def status(): Seq[Status]

  def update(status:Status)

  @throws(classOf[FloorNotExistException])
  @throws(classOf[WrongDirectionException])
  def pickup(fromFloor:Int, direction:Int)

  def step()
}
```

The scheduling algorithm used is analogous to [Shortest Seek First] (https://en.wikipedia.org/wiki/Shortest_seek_first). The algorithm, mostly covered in ControlSystemImpl.seek, goes in this order:

* For each pickup request, find an elevator that's closest distance going the same direction.
* For all unfullfilled requests, they go back to the end of the request queue. We could have put them all back to the front but starvation might happen.

**Known Issuses**

* Thread Safety - none has been considered. If we are to implement this, most likely, we could put the state of the elevator abstracted by an Akka/Actor using its built in one message at a time guarantee.
* Number of pickups per elevator - assuming it has infinite capacity is not realistic! We could put a max limit check and ignore the elevator when it reaches the capacity.
* The main program (App) is very limiting. It has one use case; however, we have quite a few unit tests which can be run.

**Worth Mentioning Implementation**

Instead of polluting the ControlSystemImpl with logging statement and switches for different logging implementations, we use Scala mixin so that we can use Logback for logging in main program and Informer in testing.
i.e. new ControlSystemImpl(numberOfElevators, maxFloor) with LoggedControlSystem

**Build, Run & Test Instructions**

If you have docker, you can just run or test like this:
*  docker run -it kykl/elevator_pitch sbt test
*  docker run -it kykl/elevator_pitch sbt run

Otherwise, you need have these build and run tools:
* sbt launcher version 0.13.7
* scala  2.11.7

and then:
* Test - sbt test
* Run - sbt run
