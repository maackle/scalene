package scalene.traits

import scalene._
import gfx.Transform
import scalene.core.Op
import collection.mutable
import collection.immutable.ListSet

trait Initialize {

  private var initializeOps = ListSet[Op]()
  private var cleanupOps = ListSet[Op]()
  protected def doInitialize() {
    initializeOps foreach { op => op() }
  }
  protected def doCleanup() {
    cleanupOps foreach { op => op() }
  }

  protected def initialize(bloc: =>Unit) {
    initializeOps += Op( bloc )
  }
  protected def cleanup(bloc: =>Unit) {
    cleanupOps += Op( bloc )
  }
}


trait EnterExit {

  private var enterOps = ListSet[Op]()
  private var exitOps = ListSet[Op]()
  protected def doEnter() {
    enterOps foreach { op => op() }
  }
  protected def doExit() {
    exitOps foreach { op => op() }
  }

  protected def onEnter(bloc: =>Unit) {
    enterOps += Op( bloc )
  }
  protected def onExit(bloc: =>Unit) {
    exitOps += Op( bloc )
  }
}

trait Simulate {
  def simulate(dt:R)
}
trait Update {
  def update()
  def __update() = update()
}

trait Render {
  def render()
  def __render() = render()
}

trait InternalTransform extends Render {
  def __transform:Transform
  override def __render() {
    __transform.apply { super.__render() }
  }
}

trait Execute {
  def execute()
  def __execute() = execute()
}



