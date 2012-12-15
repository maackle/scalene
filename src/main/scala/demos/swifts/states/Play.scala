package demos.swifts.states

import scalene.core.{View2D, State}
import demos.swifts.Swifts
import scalene.gfx.Color

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 15/12/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
class Play extends State(Swifts) {

  val swifts = List()
  val view = View2D.simple(Color(0x555555), swifts)
}
