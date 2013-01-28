package demos.heart.things

import scalene.core.traits.{Update, Render}
import scalene.event.{HandyHandlers, EventHandler, EventSink}
import scalene.components.{Verlet2D, CircleShape, Velocity2D}
import scalene.vector.vec2
import scalene.gfx
import gfx.{gl, AutoTransformer2D, Color}
import org.lwjgl.opengl.GL11


class Walker(val monitor:HeartMonitor, val position:vec2=vec2.zero)
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

  val maxHealth = 10f
  private var _health = maxHealth
  def health = _health

  def update(dt:Float) {
    monitor.aberration = (maxHealth - _health)
    monitor.update(dt)
  }

  def acceptScore(score:Float) {
    _health += score
    _health = math.min(_health, maxHealth)
  }

  def render() {
    color.bind()
    gfx.draw.circle(radius, position)
  }

  val handler = mover(velocity, speed)(KEY_UP, KEY_LEFT, KEY_DOWN, KEY_RIGHT)
}
