package demos.pong.things

import scalene.core.traits.{Update, Render}
import scalene.event._
import scalene.components.{RectangleShape, Velocity2D}
import demos.pong.things.Arena.Side
import scalene.vector.{vec2, vec}
import scalene.gfx.{AutoTransformer2D, Color, draw}
import scalene.event.KeyHoldEvent
import scalene.common
import common._

object Arena {

  object Side extends Enumeration {
    val Left, Right = Value
  }
  type Side = Side.Value

}

class Arena(val width:Real, val height:Real) {

  def paddleX(side:Arena.Side):Real = {
    val ratio = 0.9f
    side match {
      case Side.Left =>  -width/2 * ratio
      case Side.Right => +width/2 * ratio
    }
  }

}

object Paddle {
  case class Controls(upKey:Int, downKey:Int)
}

class Paddle(arena:Arena, side:Arena.Side, controls:Paddle.Controls)
  extends Velocity2D
  with RectangleShape
  with AutoTransformer2D
  with EventSink {

  val speed = 400
  val thickness = 10f
  val width = thickness
  var height = 50.0f
  val position = vec(arena.paddleX(side), 0f)
  val velocity = vec2.zero
  val rotation = 0.0
  val scale = vec2.one

  val handler = EventHandler {
    case KeyHoldEvent(controls.upKey) =>
      velocity.y = +speed
    case KeyHoldEvent(controls.downKey) =>
      velocity.y = -speed
    case KeyUpEvent(controls.upKey) | KeyUpEvent(controls.downKey) =>
      velocity.y = 0
  }

  def simulate(dt:Real) {}

  override def render() {
    Color.white.bind()
    draw.rect(thickness, height)
  }

}

class Ball(direction:Arena.Side)
  extends Velocity2D
  with RectangleShape
  with AutoTransformer2D {

  private val initialSpreadAngle = math.Pi/2 // 90 degrees
  val radius = 4.0f
  val height, width = radius

  val position = vec2.zero
  val scale = vec2.one
  val rotation = 0.0
  def velocity = vec.polar(speed, angle)

  var speed = 100
  var angle = {
    val center = direction match {
      case Side.Left => math.Pi
      case Side.Right => math.Pi * 2
    }
    val spread = initialSpreadAngle / 2
    maackle.util.Random.uniform(center - spread, center + spread)
  }

  def simulate(dt:Real) {}

  override def render() {
    Color.white.bind()
    draw.circle(radius)
  }
}