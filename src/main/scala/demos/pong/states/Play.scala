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

class Play extends State(Pong) with Worldly {

  implicit val world = new World(vec(0,0), true)

  lazy val arena = {
    val (w,h) = app.currentWindowSize
    new Arena(w, h)
  }

  val paddles = Seq(
    new Paddle(arena, Arena.Side.Left, Controls(KEY_A, KEY_Z)),
    new Paddle(arena, Arena.Side.Right, Controls(KEY_K, KEY_M))
  )

  this ++= paddles
  this += new Ball(Arena.Side.Left)

  val view = View2D.simple(Color.gray, renderables.toSeq)
}
