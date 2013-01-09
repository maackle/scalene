package scalene.core.traits

import collection.mutable
import scalene.common
import common.Real

trait ScaleneMixin
trait Component extends ScaleneMixin
trait Hook extends ScaleneMixin

trait EnterExit {
  def onEnter(bloc: =>Unit)
  def onExit(bloc: =>Unit)
}

trait Entity extends ScaleneMixin with Update with Render

trait Node {
  protected lazy val children:mutable.Set[Node] = mutable.Set[Node]()
  def +=(n:Node) { children += n }
  def -=(n:Node) { children -= n }
}


object ID {
  private var lastId=1000
  private def nextId = { lastId+=1; lastId }
}

trait ID {
  val id = ID.nextId
}


trait Execute {
  def execute()
  def __execute() = execute()
}

trait Initialize {
//  private var _isInitialized_? = false
//  protected def initialize() {
//    if(!_isInitialized_?)
//    _isInitialized_? = true
//  }
  protected def initialize()
}

trait Render extends Hook {
  def render()
  def __render() = render()
}

trait PreUpdate extends Hook {
  def preupdate()
  def __preupdate() = preupdate()
}

trait Update extends Hook {
  def update(dt:Real)
  def __update(dt:Real) = { update(dt) }
}

//trait Simulate extends Hook {
//  def simulate(dt:Real)
//  def __simulate(dt:Real) = simulate(dt)
//}
//
