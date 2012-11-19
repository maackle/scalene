package scalene.gfx

import scalene.core.traits.{Update, Render}
import scalene.vector.vec2
import scalene.gfx.SpriteAnimation.FrameOptions

trait Animation extends Render with Update {
  object Mode extends Enumeration {
    val Once, Loop, PingPong = Value
  }
  type Mode = Mode.Value
  def pause()
  def play()
}


object SpriteAnimation {

  case class FrameOptions(durationMs:Int, offset:vec2=vec2.zero)
}

trait ImageAnimation extends Animation {
  def frames:Map[Image, FrameOptions]
  def images:IndexedSeq[Image]
}

class SpriteAnimation(var position:vec2, f:(Image, FrameOptions)*) extends SpriteLike with ImageAnimation {

  var scale = vec2.one

  def this(position:vec2, images:Seq[Image], durationMs:Int) = this(position, {
    val opt = FrameOptions(durationMs, vec2.zero)
    images.map((_, opt))
  } : _*)

  val images = f.map(_._1).toIndexedSeq
  val frames = f.toMap

  private var mode = Mode.Loop
  private var currentIndex = 0
  private var direction = 1
  private val numFrames = images.size
  private var advance = true

  def image = images(currentIndex)

  def play() { advance = true }
  def pause() { advance = false }

  def update() {
    if(advance) {
      mode match {
        case Mode.Once => if(currentIndex < numFrames - 1) currentIndex += 1
        case Mode.Loop =>
          currentIndex = (currentIndex + direction) % numFrames
        case Mode.PingPong =>
          currentIndex = (currentIndex + direction) % numFrames
          direction = -direction
      }
    }
  }
}
