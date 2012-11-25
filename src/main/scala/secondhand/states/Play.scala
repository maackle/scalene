package secondhand.states

import scalene.core.{View2D, State}
import secondhand.SecondHand
import scalene.gfx.Color
import secondhand.things.{Hero, Ship}
import scalene.vector.vec
import scalene.event.HandyHandlers

class Play extends State(SecondHand) with HandyHandlers {

  val things = List(
    new Hero(vec(0,0))
  )

  lazy val handler =
    panner(view, 5)(KEY_W, KEY_A, KEY_S, KEY_D) ++
      zoomer(view, 0.99)(KEY_MINUS, KEY_EQUALS)

  this ++= things

  val view = View2D.simple(Color.gray, things)
}
