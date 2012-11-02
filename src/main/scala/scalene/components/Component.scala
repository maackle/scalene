package scalene.components

import scalene.vector._
import scalene.common
import scalene.traits.InternalTransform
import scalene.gfx.Transform

trait Component

trait Position extends Component { def position: vec }
trait Position2D extends Component {
  def position: vec2
}
trait PositionXY extends Position2D {
  def x = position.x
  def y = position.y
  def x_=(v:common.R) { position.x = v}
  def y_=(v:common.R) { position.y = v}
}

trait Rotation {
  def rotation: Radian
}

trait Scaling2D {
  def scale: vec2
}

trait Rigid2D extends InternalTransform with Position2D with Rotation {
  val __transform = Transform.dynamic(()=>position, null, ()=>rotation)
}
trait Affine2D extends InternalTransform with Position2D with Rotation with Scaling2D {
  val __transform = Transform.dynamic(()=>position, ()=>scale, ()=>rotation)
}

trait Velocity extends Component { def velocity: vec }
trait Velocity2D extends Component { def velocity: vec2 }

trait Collider extends Position2D