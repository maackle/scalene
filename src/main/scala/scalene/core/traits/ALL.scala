package scalene.core.traits

import collection.mutable
import scalene.common
import common.Real

trait Component

trait EnterExit {
  def onEnter(bloc: =>Unit)
  def onExit(bloc: =>Unit)
}

trait Entity extends Component with Update with Render

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

trait Render extends Component {
  def render()
  def __render() = render()
}

trait PreUpdate extends Component {
  def preupdate()
  def __preupdate() = preupdate()
}

trait Update extends Component {
  def update()
  def __update() = update()
}

trait Simulate extends Component {
  def simulate(dt:Real)
  def __simulate(dt:Real) = simulate(dt)
}

