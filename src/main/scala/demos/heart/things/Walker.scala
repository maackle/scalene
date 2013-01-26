package demos.heart.things

import scalene.core.traits.{Update, Render}
import scalene.event.{HandyHandlers, EventHandler, EventSink}
import scalene.components.{Verlet2D, CircleShape, Velocity2D}
import scalene.vector.vec2
import scalene.gfx
import gfx.{AutoTransformer2D, Color}

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/26/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
class Walker(val position:vec2=vec2.zero)
  extends Update
  with Render
  with Verlet2D
  with CircleShape
  with HandyHandlers {

  val speed = 30f
  val rotation = 0.0
  val scale = vec2.one
  val acceleration = vec2.zero
  val velocity = vec2.zero
  val radius = 1f
  val color = Color.blue

  def update(dt:Float) {
  }

  def render() {
    color.bind()
    gfx.draw.circle(radius, position)
  }

  val handler = mover(velocity, speed)(KEY_UP, KEY_LEFT, KEY_DOWN, KEY_RIGHT)
}
