package demos.pong

import scalene.core.ScaleneApp

object Pong extends ScaleneApp {
  val windowSize = Some(400,400)
  val windowTitle = "Scalene Pong"
  lazy val startState = new states.Play()
}
