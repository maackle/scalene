package demos.heart

import scalene.core.{ScaleneAppDebug, ScaleneApp}
import scalene.gfx.Color

object HeartGame extends ScaleneAppDebug {
  val windowSize = None//Some(400,400)
  val windowTitle = "<3"
  lazy val startState = new states.Play()
  val debugColor = Color.white
  override def extraDebugText = "health: %s\n".format(startState.walker.health)
}

