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
import scalene.physics.Physical
import org.jbox2d.dynamics.{BodyType, BodyDef}

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

  case class Wall(a:vec2, b:vec2) extends Physical with RectangleShape {
    val (initialPosition, width, height) = {
      val shape = RectangleShape(a,b)
      (shape.position, shape.width, shape.height)
    }

    override val setupBody = { body:BodyDef => {
        body.`type` = BodyType.STATIC
      }
    }
  }

  val walls = List(
    Wall(vec(-width/2, -height/2), vec(-width, +height/2)),
    Wall(vec(+width/2, -height/2), vec(+width, +height/2)),
    Wall(vec(-width/2, -height/2), vec(+width/2, -height)),
    Wall(vec(-width/2, +height/2), vec(+width/2, +height))
  )

}

object Paddle {
  case class Controls(upKey:Int, downKey:Int)
}

class Paddle(arena:Arena, side:Arena.Side, controls:Paddle.Controls)
  extends Physical
  with Render
  with RectangleShape
  with AutoTransformer2D
  with EventSink {

  val speed = 50
  val thickness = 1f
  val width = thickness
  var height = 5f
  val initialPosition = vec(arena.paddleX(side), 0f)
//  val velocity = vec2.zero
//  val rotation = 0.0
  val scale = vec2.one

  val handler = EventHandler {
    case KeyHoldEvent(controls.upKey) =>
      velocity.y = +speed
    case KeyHoldEvent(controls.downKey) =>
      velocity.y = -speed
    case KeyUpEvent(controls.upKey) | KeyUpEvent(controls.downKey) =>
      velocity.y = 0
  }

  override val setupBody = { body:BodyDef => {
      body.`type` = BodyType.KINEMATIC
    }
  }


  def simulate(dt:Float) = ()

  def render() {
    Color.white.bind()
    draw()
  }

}

class Ball(direction:Arena.Side)
  extends Physical
  with RectangleShape
  with AutoTransformer2D {

  private val initialSpreadAngle = math.Pi/2 // 90 degrees
  val radius = 1f
  val height, width = radius

  val scale = vec2.one

  var speed = 50
  var angle = {
    val center = direction match {
      case Side.Left => math.Pi
      case Side.Right => math.Pi * 2
    }
    val spread = initialSpreadAngle / 2
    maackle.util.Random.uniform(center - spread, center + spread)
  }

  val initialPosition = vec2.zero
  override val initialVelocity = vec.polar(speed, angle)

  def update(dt:Float) = ()

  override val setupBody = { body:BodyDef =>
    body.angularDamping = 0.5f

  }

  override def render() {
    Color.white.bind()
    draw()
  }
}