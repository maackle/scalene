package demos.pong.states

import scalene.core.{View2D, State}
import scalene.gfx.Color
import demos.pong.things.{Ball, Arena, Paddle}
import demos.pong.Pong
import demos.pong.things.Paddle.Controls
import scalene.input.Keys._
import scalene.core.traits.Update
import scalene.physics.Worldly
import org.jbox2d.dynamics.World
import scalene.vector.vec
import org.jbox2d.dynamics
import scalene.event.{KeyHoldEvent, KeyDownEvent, EventHandler, HandyHandlers}

class Play extends State(Pong) with Worldly with HandyHandlers {

  implicit val world = new dynamics.World(vec(0,0), true)

  val scale = 10f

  lazy val arena = {
    val (w,h) = app.currentWindowSize
    new Arena(w/scale, h/scale)
  }

  val paddles = Seq(
    new Paddle(arena, Arena.Side.Left, Controls(KEY_W, KEY_S, KEY_A, KEY_D)),
    new Paddle(arena, Arena.Side.Right, Controls(KEY_UP, KEY_DOWN, KEY_LEFT, KEY_RIGHT))
  )

  val ball = new Ball(Arena.Side.Left)

  this ++= paddles
  this += ball
  this ++= arena.walls

  val view = View2D.simple(Color.gray, renderables.toSeq)
  view.zoom = math.sqrt(scale).toFloat

  val handler = zoomer(view, 0.99f)(KEY_MINUS, KEY_EQUALS) ++ EventHandler {
    case KeyHoldEvent(KEY_G) =>
      ball.dScale *= 0.90f
    case KeyHoldEvent(KEY_H) => ball.dScale /= 0.90f
  }
}
