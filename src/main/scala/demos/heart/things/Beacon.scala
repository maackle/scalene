package demos.heart.things

import scalene.core.traits.{Render, Update}
import scalene.audio.SoundStore
import scalene.core.State
import scalene.helpers.Memory
import scalene.vector.vec2
import demos.heart.TimeSync

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/26/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
class Beacon(val position:vec2)(implicit val state:demos.heart.states.Play)
  extends Render
  with Update
  with TimeSync {

  val sounds = new SoundStore()

  sounds.addSource("snd/drum/kick.wav", "kick", false)
  sounds.addSource("snd/drum/snare.wav", "snare", false)

  val ts = new Memory[Float](2, 0)

  def update(dt:Float) {
    ts << T % period
    if(ts.now < ts.prev) sounds("kick").play()
  }

  def render() {

  }
}
