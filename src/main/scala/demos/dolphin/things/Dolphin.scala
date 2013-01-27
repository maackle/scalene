package demos.dolphin.things

import scalene.core.traits.{Update, Render}
import scalene.event.{KeyUp, KeyDown, EventHandler, EventSink}
import scalene.components.Velocity2D
import scalene.vector.{vec, vec2}
import scalene.gfx._
import org.lwjgl.opengl.GL11
import scalene.common
import common._
import scalene.core.Resource

class Dolphin extends {
  val ss = new SpriteSheet(Image("img/dolphin/ecco.png"), 1, 6)
} with SpriteAnimation(vec2.zero, ss.images, 40) with EventSink {

  pause()

  val handler = EventHandler {
    case KeyDown(KEY_SPACE) => play()
    case KeyUp(KEY_SPACE) => pause()
  }

}