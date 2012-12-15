package demos.pacman

import scalene.core.ScaleneApp

object Pacman extends ScaleneApp {
  val windowSize = Some(400,400)
  val windowTitle = "Pacman"
  lazy val startState = new states.Play()
}
