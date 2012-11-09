package run

import run.domain._
import scalene.core.ScaleneApp

object Run extends {

  val windowSize = Some(600,600)
  val windowTitle = "SCALENE GAME"

} with ScaleneApp {

  lazy val startState = SnailDomain.SnailState
}
