package secondhand.things

import scalene.components.Acceleration2D
import scalene.core.traits.{Update, Render}
import scalene.vector.vec2
import scalene.event._
import scalene.common._
import scalene.gfx
import gfx.{AutoTransformer2D, Image, Spritely}
import scalene.core.Resource

abstract class Ship
  extends Spritely
  with Acceleration2D

object Hero {
  val accel = 1000.0
  val friction = 1
}

class Hero(var position:vec2) extends Ship with EventSink with HandyHandlers {

  import Hero._

  val imageResource = Resource("img/ship.png")(Image.load)
  var velocity, acceleration = vec2.zero
  def rotation = (velocity).angle - math.Pi/2
  def translate = position

  def handler = (
    mover(acceleration, accel)(KEY_UP, KEY_LEFT, KEY_DOWN, KEY_RIGHT)
  )

  def simulate(dt:Real) = {
    velocity *= (1 - friction * dt)
  }

}
