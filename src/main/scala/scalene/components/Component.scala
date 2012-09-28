package scalene.components

import scalene.vector._
import scalene.common

trait Component

trait Position extends Component { def position: vector }
trait Position2D extends Component {
  def position: vec2
  def x = position.x
  def y = position.y
  def x_=(v:common.R) { position.x = v}
  def y_=(v:common.R) { position.y = v}
}

trait Velocity extends Component { def velocity: vector }
trait Velocity2D extends Component { def velocity: vec2 }

trait Collider extends Position2D