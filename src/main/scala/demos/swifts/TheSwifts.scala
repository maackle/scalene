package demos.swifts

import scalene.core.{ScaleneAppDebug, ScaleneApp}
import scalene.gfx.Color

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 15/12/12
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
object TheSwifts extends ScaleneApp with ScaleneAppDebug {
  val windowSize = Some(500,500)
  val windowTitle = "The Swifts"

  lazy val startState = new states.MenuState

  val debugColor = Color.black
}
