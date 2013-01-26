package demos.heart.states

import scalene.core.{Layer2D, View2D, State}
import demos.heart.HeartGame
import scalene.event.{EventHandler, EventSink}
import scalene.gfx.Color
import demos.heart.things.{Walker, HeartMonitor}
import scalene.vector.vec2

class Play extends State(HeartGame) with EventSink {

  val monitor = new HeartMonitor(vec2.zero, (300,300))
  val walker = new Walker

  this += monitor
  this += walker


  val view = View2D(Color.gray)(
    Layer2D(monitor),
    Layer2D(walker)
  )

  view.zoom = 1

  val handler = EventHandler {
    case _ =>
  }
}

