package demos.heart.states

import scalene.core.{LayerHUD, Layer2D, View2D, State}
import demos.heart.HeartGame
import scalene.event.{KeyDownEvent, KeyHoldEvent, EventHandler, EventSink}
import scalene.gfx.Color
import demos.heart.things.{RhythmPattern, Beacon, Walker, HeartMonitor}
import scalene.vector.{vec, vec2}
import scalene.core.traits.Update
import scalene.audio.SoundStore
import org.lwjgl.openal.{AL11, AL10}

class Play extends State(HeartGame) with EventSink with Update {
  implicit val state = this

  val monitor = new HeartMonitor(vec2.zero, (app.currentWindowSize._1.toFloat, app.currentWindowSize._2.toFloat))
  val walker = new Walker

  val tempo = 100f
  var time = 0f

  AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED)

  val fourOnFloor = new RhythmPattern(4, List(
    'b -> 0,
    'b -> 4,
    'b -> 8,
    'b -> 12,
    'b -> 24,
    'b -> 36
  ))

  val oompah = new RhythmPattern(4, List(
    'b -> 0,
    's -> 12,
    'b -> 24,
    's -> 36
  ))

  val hihats = new RhythmPattern(4,
    ((0 to 48 by 3) map ( 'z -> _ ))
  )

  lazy val beacons = List (
    new Beacon(vec(10,10), fourOnFloor),
    new Beacon(vec(100,100), oompah),
    new Beacon(vec(300,-100), hihats)
  )

  override def update(dt:Float) {
    time += dt
    view.scroll = walker.position * view.zoom
    SoundStore.setListenerPos(walker.position)
  }

  this += monitor
  this += walker
  this ++= beacons

  val view = View2D(Color.gray)(
    new LayerHUD(monitor :: Nil),
    Layer2D(beacons),
    Layer2D(walker)
  )

  view.zoom = 10

  def closestBeacon = {
    beacons.minBy { b =>
      (b.position - walker.position).length
    }
  }

  val handler = EventHandler {
    case KeyDownEvent(KEY_SPACE) => closestBeacon.registerHit()

//    case KeyHoldEvent(KEY_COMMA) => tempo -= 1
//    case KeyHoldEvent(KEY_PERIOD) => tempo += 1
    case _ =>
  }
}

