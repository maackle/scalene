package demos.swifts

import scalene.core.ScaleneApp

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 15/12/12
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
object Swifts extends ScaleneApp {
  val windowSize = Some(600,600)
  val windowTitle = "The Swifts"

  lazy val startState = new states.Play
}
