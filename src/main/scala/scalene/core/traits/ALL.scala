package scalene.core.traits

import collection.mutable
import scalene.core.Op
import scalene.gfx.Transform
import collection.immutable.ListSet
import scalene.common
import common.Real

trait EnterExit {
  def onEnter(bloc: =>Unit)
  def onExit(bloc: =>Unit)
}


trait Entity extends Thing with Update with Render

trait Node {
  protected lazy val children:mutable.Set[Node] = mutable.Set[Node]()
  def +=(n:Node) { children += n }
  def -=(n:Node) { children -= n }
}


trait Thing



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

trait Render {
  def render()
  def __render() = render()
}

trait PreUpdate {
  def preupdate()
  def __preupdate() = preupdate()
}

trait Update {
  def update()
  def __update() = update()
}

trait Simulate {
  def simulate(dt:Real)
  def __simulate(dt:Real) = simulate(dt)
}

