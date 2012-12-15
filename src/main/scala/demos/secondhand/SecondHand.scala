package demos.secondhand

import demos.secondhand.states.Play
import scalene.core.{ScaleneAppDebug, ScaleneApp}
import scalene.gfx.Color

object SecondHand extends ScaleneApp {
  val windowSize = Some(800,800)
  val windowTitle = "Second Hand"
  val debugColor = Color.white

  lazy val startState = new Play()
}
