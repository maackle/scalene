package scalene.traits

import scalene.components.EventSource
import scalene.core.ScaleneApp


abstract class State(val app:ScaleneApp) extends ThingStore with EnterExit with Render {
  def this(domain:Domain) = this(domain.app)
  val views:ViewScheme

//  def update() {
//    input.update()
//  }
//  def execute() {
//    input.execute()
//  }
  def render() {
    views.__render()
  }
//  def sound:Option[SoundStore]
}