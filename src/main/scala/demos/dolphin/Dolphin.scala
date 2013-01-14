package demos.dolphin

import scalene.core.ScaleneApp

object Dolphin extends ScaleneApp {
  val windowSize = Some(400,400)
  val windowTitle = "Dolphin"
  lazy val startState = new states.Play()
}
