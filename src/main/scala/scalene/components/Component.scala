package scalene.components

import scalene.vector._
import scalene.common
import scalene.core.traits.{Simulate, Update, InternalTransform}
import scalene.gfx.Transform

trait Component

trait Position extends Component { def position: vec }
trait Position2D extends Component {
  def position: vec2
}
trait PositionXY extends Position2D {
  def x = position.x
  def y = position.y
  def x_=(v:common.Real) { position.x = v}
  def y_=(v:common.Real) { position.y = v}
}


trait Rotation {
  def rotation: Radian
}

trait Scaling2D {
  def scale: vec2
}


trait Velocity extends Component { def velocity: vec }
trait Velocity2D extends Component with Position2D with Simulate {
  def velocity: vec2
  override def __simulate(dt:common.Real) {
    position += velocity * dt
    super.__simulate(dt)
  }
}

trait Acceleration extends Component { def acceleration: vec }
trait Acceleration2D extends Component with Velocity2D {
  def acceleration: vec2
  override def __simulate(dt:common.Real) {
    velocity += acceleration * dt
    super.__simulate(dt)
  }
}

trait Collider extends Position2D