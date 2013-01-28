package demos.heart.things

import scalene.core.traits.{Render, Update}
import scalene.audio.{ALsource, SoundStore}
import scalene.core.State
import scalene.helpers.Memory
import scalene.vector.vec2
import demos.heart.TimeSync
import org.lwjgl.openal.AL10
import scalene.components.CircleShape
import scalene.gfx
import scalene.gfx.{Sprite, Color}
import demos.heart.things.RhythmPattern.Hit
import org.lwjgl.opengl.GL11

class RhythmPattern(val beatValue:Int, val hits:Seq[RhythmPattern.Hit]) {
  def +(p:RhythmPattern) = {
    new RhythmPattern(beatValue, hits.toList ::: p.hits.toList)
  }
}

object RhythmPattern {
  val ticksPerMeasure = 48
  case class Hit(symbol:Symbol, offset:Int, gain:Float=1f)
}

class Ripple(val beacon:Beacon, theOne:Boolean) extends Update {
  var radius = beacon.radius
  val maxRadius = beacon.maxDistance
  val speed = 60f

  val color = beacon.color

  def draw() {
    gfx.draw.fill(true)
    val r = 0.5f*(maxRadius - radius)/maxRadius
    color.alpha(r).bind()
    if (theOne) {
      scalene.gfx.draw.lineWidth(4f)
    }
    else {
      scalene.gfx.draw.lineWidth(2f)
    }
    val shape = scalene.gfx.draw.getCircle(32)
    gfx.gl.matrix {
      gfx.gl.translate(beacon.position)
      gfx.gl.scale(radius, radius)
      gfx.gl.begin(GL11.GL_TRIANGLE_FAN) {
        Color.white.alpha(0).bind()
        gfx.gl.vertex(0,0)
        beacon.color.alpha(r).bind()
        gfx.draw.bindVertices(shape)
        gfx.gl.vertex(shape.head)
      }
    }
//    scalene.gfx.draw.circle(radius, beacon.position, 32)
  }

  def update(dt:Float) {
    radius += speed * dt
  }
}

class Beacon(val position:vec2, val tolerance:Float, pattern:RhythmPattern)(implicit val state:demos.heart.states.Play)
  extends Render
  with Update
  with CircleShape
  with TimeSync { beacon =>

  val radius = 2f
  val maxDistance = 100f
  def color = if (state.activeBeacon==this) Color.green else Color.cyan

  lazy val sounds = {
    val ss = new SoundStore()
    ss.setDefaultAttenuation(radius, maxDistance, 1f)
    ss.addSource("snd/drum/kick2.wav", "kick", false)
    ss.addSource("snd/drum/snare.wav", "snare", false)
    ss.addSource("snd/drum/1.wav", "zit", false)
    ss
  }

  var ripples = collection.mutable.Set[Ripple]()

  private def nameMap(sym:Symbol) = sym match {
    case 's => "snare"
    case 'b => "kick"
    case 'z => "zit"
  }

  def secondsToTick(t:Float) = (((t/pattern.beatValue) % period)/period * RhythmPattern.ticksPerMeasure)
  def tick = secondsToTick(T).toInt
  val ticks = new Memory[Int](2, 0)

  def update(dt:Float) {
    ticks << tick
    var (t0, t1) = (ticks.prev, ticks.now)
    if (t1 < t0) {
      t0 -= RhythmPattern.ticksPerMeasure
    }
    for (hit <- pattern.hits; RhythmPattern.Hit(s, t, vol) = hit) {
      if (t0 <= t && t < t1 ) {
        sounds(nameMap(s)).volume(vol).setPos(position).play()
        ripples += new Ripple(this, t==0)
      }
    }
    for (ripple <- ripples) ripple.update(dt)
  }

  def render() {
    drawRipples()
    gfx.draw.fill(true)
    color.bind()
    scalene.gfx.draw.circle(radius, position, 8)
  }

  def drawRipples() {
    for (ripple <- ripples) {
      ripple.draw()
      if (ripple.radius > ripple.maxRadius) ripples -= ripple
    }
  }

  def registerHit() = {
    val t1 = tick - HitTracker.latencyCorrectionTicks
    var good = false
    for (hit <- pattern.hits) {
      if (math.abs(hit.offset - t1) < tolerance) {
        good = true
        HitTracker.good(hit, t1)
      }
    }
    if( ! good) {
      HitTracker.bad(t1)
    }
  }

  object HitTracker {
    val m:collection.mutable.Map[Hit, Float] = collection.mutable.Map( pattern.hits.map((_,-1f)):_* )

    val latencyCorrectionTicks = secondsToTick(latencyCorrection)
    val passFactor = 3f
    val missFactor = 1f

    private var _score = 0f
    private var _misses = 0
    def lastScore = _score

    def reset() {
      setScore()
      for((k,v) <- m) m(k) = -1
      _misses = 0
    }

    def good(hit:Hit, t:Float) {
      m(hit) = math.abs(hit.offset.toFloat - (t.toFloat))
    }

    def bad(t:Float) {
      _misses += 1
    }

    private def setScore() = {
      _score = ( for ( (hit, accuracy) <- m ) yield {
        if(accuracy > 0) (tolerance - accuracy).toFloat / tolerance
        else 0f
      }).sum * passFactor / pattern.hits.length
      _score -= _misses.toFloat * missFactor / pattern.hits.length * distanceFn

      def distanceFn() = {
        val d = (state.walker.position - position).length / maxDistance
        math.min(1f, d)
      }
    }

    reset()
  }
}
