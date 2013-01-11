package demos.pacman.states

import scalene.core.{View2D, State}
import demos.pacman.{Pacman => Game}
import scalene.gfx.Color
import demos.pacman.things.Pacman
import scalene.vector.vec
import scalene.event.{KeyDownEvent, EventHandler, EventSink}
import scalene.misc.Easing

class Play extends State(Game) with EventSink {

  val pacman = new Pacman(vec(0,0))
  this += pacman

  val transition = Transition.create(10, Easing.exponential(2))(t => pacman.rotation = t * 2 * math.Pi)

  val view = View2D.simple(Color.gray, renderables.toSeq)

  val handler = EventHandler {
    case KeyDownEvent(KEY_SPACE) => transition.start()
  }
}
