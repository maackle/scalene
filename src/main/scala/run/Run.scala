package run

import domain.SnailDomain
import scalene.core.{ScaleneAppDebug, ScaleneApp}
import scalene.gfx.Color

object Run extends {

  val windowSize = Some(600,600)
  val windowTitle = "SCALENE GAME"

} with ScaleneApp with ScaleneAppDebug {

  val debugColor = Color.black
  lazy val startState = SnailDomain.SnailState
//  lazy val startState = new Tomboy.PlayTomboy

}
