package demos.swifts.states

import scalene.core.{Op, View2D, State}
import demos.swifts.TheSwifts
import scalene.gfx.Color
import scalene.event._
import scalene.event.KeyDown

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/23/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
class PauseState(parentState:State) extends State(TheSwifts) with EventSink {

  val view = null

  override def render() {
    parentState.render()
  }

  val handler = EventHandler {
    case KeyDown(KEY_P) =>
      popState()
  }
}
