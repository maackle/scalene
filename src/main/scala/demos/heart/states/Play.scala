package demos.heart.states

import scalene.core._
import demos.heart.HeartGame
import scalene.event._
import scalene.gfx.{gl, Color}
import demos.heart.things.{RhythmPattern, Beacon, Walker, HeartMonitor}
import scalene.vector.{vec, vec2}
import traits.{Render, Update}
import scalene.audio.SoundStore
import org.lwjgl.openal.{AL11, AL10}
import scalene.helpers.Memory
import scalene.event.KeyDown
import scalene.misc.SolidBackground
import org.lwjgl.opengl.{GL12, GL11}

class Play extends StateIndexed(HeartGame) with EventSink with Update {
  implicit val state = this

  val latencyCorrection = .105f
  val walker = new Walker(
    new HeartMonitor(vec(0, app.currentWindowSize._2.toFloat), (app.currentWindowSize._1.toFloat, app.currentWindowSize._2.toFloat / 2))
  )
  val tempo = 100f
  def period = 60f / tempo
  var time = 0f
  private val ts = new Memory(2, 0f)

  def onTheOne:Boolean = {
    (ts.now % (period*4)) < (ts.prev % (period*4))
  }

  AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED)

  import RhythmPattern.Hit
  val fourOnFloor = new RhythmPattern(4, List(
    Hit('b, 0),
    Hit('b, 12),
    Hit('b, 24),
    Hit('b, 36)
  ))

  val oompah = new RhythmPattern(4, List(
    Hit('b, 0),
    Hit('s, 12),
    Hit('b, 24),
    Hit('s, 36)
  ))

  val clave = new RhythmPattern(4, List(
    Hit('z, 6),
    Hit('z, 12),
    Hit('z, 24),
    Hit('z, 33),
    Hit('z, 42)
  ))

  val guiro = new RhythmPattern(4, List(
    Hit('s, 0),
    Hit('s, 18),
    Hit('s, 30)
  ))

  val hihats = new RhythmPattern(4,
    ((0 to 48 by 3) map ( Hit('z, _ )))
  )

  lazy val beacons = List (
    new Beacon(vec(10,10), 1f, fourOnFloor),
    new Beacon(vec(100,100), 1f, clave),
    new Beacon(vec(100,-100), 1f, guiro),
    new Beacon(vec(100,-200), 0.25f, hihats)
  )

  override def update(dt:Float) {
    time += dt
    ts << time
    view.scroll = walker.position * view.zoom
    SoundStore.setListenerPos(walker.position)

    if (onTheOne) {
      beacons.foreach( _.HitTracker.reset() )
      val activeScore = activeBeacon.HitTracker.lastScore
      val closestScore = closestBeacon.HitTracker.lastScore

      if(closestScore > activeScore && closestScore > 0) {
        activeBeacon = closestBeacon
      }

      val score = activeBeacon.HitTracker.lastScore
      walker.acceptScore(score)
    }
  }

  this += Background
  this += walker
  this ++= beacons
  this += this

  val view = View2D(Color.gray)(
    Layer2D(Background :: Nil),
    new LayerHUD(walker.monitor :: Nil),
    Layer2D(beacons),
    Layer2D(walker)
  )

  view.zoom = 10

  def closestBeacon = {
    beacons.minBy { b =>
      (b.position - walker.position).length
    }
  }

  var activeBeacon:Beacon = beacons.head

  val handler = EventHandler {
    case KeyDown(KEY_SPACE) => closestBeacon.registerHit()
    case KeyDown(key) =>
      if(key==KEY_Z || key==KEY_X || key==KEY_C || key==KEY_V) closestBeacon.registerHit()
//    case KeyHoldEvent(KEY_COMMA) => tempo -= 1
//    case KeyHoldEvent(KEY_PERIOD) => tempo += 1
    case _ =>
  }

  object Background extends Render with Update {
    val healthColor = Color.green.alpha(0.5f)
    val hurtColor = Color.red.alpha(0.33f)
    val normalColor = Color.gray.alpha(0)
    var healthFlash = 0f
    var hurtFlash = 0f
    def score = state.activeBeacon.HitTracker.lastScore

    def update(dt:Float) {
      healthFlash *= 0.8f
      hurtFlash *= 0.99f
      if(onTheOne) {
        if(score > 0) healthFlash = 1f
        if(score < 0) hurtFlash = 1f
      }
    }

    def drawGears() {
      val w = state.app.currentWindowSize._1 / state.view.zoom
      val h = state.app.currentWindowSize._2 / state.view.zoom
      val spacing = 30
      val size = 15f
      scalene.gfx.draw.lineWidth(2f)
      val xx = walker.position.x.toInt / spacing * spacing
      val yy = walker.position.y.toInt / spacing * spacing
      val shape = for {
        i <- 0 to 8
      } yield {
        val m = {
          if(i%2==0) 1.2f
          else 0.5f
        }
        val t = math.Pi * 2 * i / 8
        vec.polar(m*size, t)
      }
      Color.black.alpha(0.15f).bind()
      gl.fill(true)
      for ((x,i) <- (-w+xx to w+xx by spacing).view.zipWithIndex) {
        for ((y,j) <- (-h+yy to h+yy by spacing).view.zipWithIndex) {
          gl.matrix {
            val polarity = {
              var v = (x/spacing+y/spacing).toInt % 2 == 0
              if(x < 0) v = !v
              if(y < 0) v = !v
              v
            }
            val angle = (time%(4*period))/(4*period)*math.Pi*2
            gl.translate(x,y)
            gl.rotateRad(if(polarity) angle else -(angle+math.Pi/4))
            gl.begin(GL11.GL_POLYGON) {
              scalene.gfx.draw.bindVertices(shape.tail)
            }
          }
        }
      }
    }

    def drawFlash() {
      val (w,h) = state.app.currentWindowSize
      val color = {
        if(hurtFlash > healthFlash) Color.lerp(normalColor, hurtColor, hurtFlash)
        else Color.lerp(normalColor, healthColor, healthFlash)
      }

      gl.fill(true)
      gl.matrix {
        GL11.glLoadIdentity()
        color.bind()
        scalene.gfx.draw.rect(vec(-w,-h), vec(w,h))
      }
    }

    def render() {

      drawFlash()
      drawGears()


    }
  }
}

