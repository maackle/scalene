package demos.heart.things

import scalene.core.traits.{Render, Update}
import scalene.audio.{ALsource, SoundStore}
import scalene.core.State
import scalene.helpers.Memory
import scalene.vector.vec2
import demos.heart.TimeSync
import org.lwjgl.openal.AL10
import scalene.components.CircleShape
import scalene.gfx.{Sprite, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/26/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */

class RhythmPattern(val beatValue:Int, val hits:Seq[(Symbol, Int)]) {
//  private var acc = 0
//  val hits:Seq[(Int, String)] = ( for ((s, offset) <- hitdefs) yield {
//    val name = s match {
//      case 's => "snare"
//      case 'b => "kick"
//      case 'z => "zit"
//    }
//    val p = offset -> name
//    p
//  })

  def +(p:RhythmPattern) = {
    new RhythmPattern(beatValue, hits.toList ::: p.hits.toList)
  }
}

object RhythmPattern {
//  val ticksPerBeat = 12
  val ticksPerMeasure = 48

}

class Beacon(val position:vec2, pattern:RhythmPattern)(implicit val state:demos.heart.states.Play)
  extends Render
  with Update
  with CircleShape
  with TimeSync {

//  import RhythmPattern.sounds

  lazy val sounds = {
    val ss = new SoundStore()
    ss.setDefaultAttenuation(radius, 100f, 1f)
    ss.addSource("snd/drum/kick2.wav", "kick", false)
    ss.addSource("snd/drum/snare.wav", "snare", false)
    ss.addSource("snd/drum/1.wav", "zit", false)
    ss
  }

  val radius = 2f
  val color = Color.red

  private def nameMap(sym:Symbol) = sym match {
    case 's => "snare"
    case 'b => "kick"
    case 'z => "zit"
  }

  def tick = (((T/pattern.beatValue) % period)/period * RhythmPattern.ticksPerMeasure).toInt

  val ts = new Memory[Int](2, 0)

  def update(dt:Float) {
    ts << tick
    for ((s, t) <- pattern.hits) {
      if (ts.prev <= t && t < ts.now ) {
        sounds(nameMap(s)).setPos(position.x, position.y, 0).play()
      }
    }
    if(ts.now < ts.prev) {
//      pattern.hits.get(0) map { name => sounds(name).play() }
    }
  }

  def render() {
    color.bind()
    scalene.gfx.draw.circle(radius, position)
  }

  def registerHit() = {

  }
}
