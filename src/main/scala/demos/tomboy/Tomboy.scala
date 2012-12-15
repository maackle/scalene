package demos.tomboy

import scalene.core.ScaleneApp

object Tomboy extends ScaleneApp {
  val windowSize = Some(600,600)
  val windowTitle = "Tomboy"

  lazy val startState = new states.Tomboy.PlayTomboy()
}