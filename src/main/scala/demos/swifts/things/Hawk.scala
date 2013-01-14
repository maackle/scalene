package demos.swifts.things

import scalene.gfx.{Image, Sprite}
import scalene.core.Resource
import scalene.vector.{vec2, vec}
import scalene.components.{Verlet2D, Acceleration2D, Rotation}
import scalene.event.HandyHandlers

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 16/12/12
 * Time: 4:20 AM
 * To change this template use File | Settings | File Templates.
 */


trait Birdy extends Verlet2D with Rotation {

}

object Hawk {
  val maxVelocity = 280f
}

class Hawk extends Sprite(Image("img/hawks/1-64.png"), vec(0,0))
with Birdy
with HandyHandlers {
  import Hawk._
  var velocity, acceleration = vec2.zero

  def update(dt:Float) {
    rotation = velocity.angle
    velocity = velocity.limit(maxVelocity)
  }

  val handler = mover(acceleration, 1000)(KEY_UP, KEY_LEFT, KEY_DOWN, KEY_RIGHT)

}
