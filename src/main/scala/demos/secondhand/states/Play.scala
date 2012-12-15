package demos.secondhand.states

import scalene.core.{Layer2D, View2D, State}
import scalene.gfx.{draw, Color}
import scalene.vector.vec
import scalene.event.HandyHandlers
import scalene.core.traits.Render
import demos.secondhand.SecondHand
import demos.secondhand.things.Hero

class Play extends State(SecondHand) with HandyHandlers {

  val things = List (
    new Hero(vec(0,0))
  )

  object Grid extends Render {
    def render() {
      Color.white.bind()
      draw.line(vec(-1000,0), vec(1000,0))
      draw.line(vec(0, -1000), vec(0, 1000))
    }
  }

  lazy val handler =
    panner(view, 5)(KEY_W, KEY_A, KEY_S, KEY_D) ++
      zoomer(view, 0.99f)(KEY_MINUS, KEY_EQUALS)

  this ++= things

  val view = View2D(Color.gray)(Layer2D(1, Grid), Layer2D(1, things))
}
