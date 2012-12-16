package demos.swifts.things

import scalene.components.{Acceleration2D, Rotation, Position2D}
import scalene.vector.{vec2, vec}
import scalene.gfx.{draw, SpriteAnimation}
import scalene.core.traits.Render

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 15/12/12
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */

object Swift {
  val frames = (
    ""

    )
}

class Swift(var position:vec2) extends Acceleration2D with Rotation with Render {

  var rotation:Double = 0
  var velocity = position.rotate(math.Pi/2).unit * 20 * 0
  var acceleration = vec(0,0)

  val maxVelocity = 30

  def simulate(dt:Float) {
    rotation = velocity.angle

    acceleration = -position.unit * 100
    velocity = velocity.limit(maxVelocity)
  }

  def render() {
    draw.rect(position,8,8)
  }
}
