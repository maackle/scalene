package run

import run.domain._
import scalene.core.ScaleneApp

object Run extends {

  val windowSize = Some(600,600)
  val windowTitle = "SCALENE GAME"
//  val startState = BlobDomain.PlayState

} with ScaleneApp {

  lazy val startState = new VBODomain.VBOTestState
}
