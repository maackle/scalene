package demos.heart

import scalene.core.ScaleneApp

object HeartGame extends ScaleneApp {
  val windowSize = Some(400,400)
  val windowTitle = "<3"
  lazy val startState = new states.Play()
}

