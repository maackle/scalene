package secondhand

import scalene.core.{ScaleneAppDebug, ScaleneApp}
import scalene.gfx.Color
import scalene.event.HandyHandlers

object SecondHand extends ScaleneApp with ScaleneAppDebug {
  val windowSize = Some(800,800)
  val windowTitle = "Second Hand"
  val debugColor = Color.white

  lazy val startState = new states.Play()
}
