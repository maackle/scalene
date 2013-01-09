package demos.pong

import scalene.core.{ScaleneAppDebug, ScaleneApp}
import scalene.gfx.Color

object Pong extends ScaleneAppDebug {
  val debugColor = Color.white
  override def extraDebugText = startState.view.zoom.toString
  val windowSize = Some(400,400)
  val windowTitle = "Scalene Pong"
  lazy val startState = new states.Play()
}
