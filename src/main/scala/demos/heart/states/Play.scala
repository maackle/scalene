package demos.heart.states

import scalene.core.{LayerHUD, Layer2D, View2D, State}
import demos.heart.HeartGame
import scalene.event.{EventHandler, EventSink}
import scalene.gfx.Color
import demos.heart.things.{Beacon, Walker, HeartMonitor}
import scalene.vector.{vec, vec2}
import scalene.core.traits.Update

class Play extends State(HeartGame) with EventSink with Update {
  implicit val state = this

  val monitor = new HeartMonitor(vec2.zero, (300,300))
  val walker = new Walker

  var tempo = 60f
  var time = 0f

  lazy val beacons = List(
    new Beacon(vec(10,10))
  )

  override def update(dt:Float) {
    time += dt
  }

  this += monitor
  this += walker
  this ++= beacons

  val view = View2D(Color.gray)(
    new LayerHUD(monitor :: Nil),
    Layer2D(walker)
  )

  view.zoom = 10

  val handler = EventHandler {
    case KeyHold(KEY_COMMA) => tempo -= 1
  }
}

