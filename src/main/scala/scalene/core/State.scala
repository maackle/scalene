package scalene.core

import traits.{Hook, Render}
import scalene.event.EventSink
import scalene.input.LWJGLKeyboard

trait BareState {
  protected def onEnter(bloc: =>Unit) {}
  protected def onExit(bloc: =>Unit) {}
}


abstract class ScaleneSketch
  extends ScaleneApp
  with StateMixin
  with EventSink
  with LWJGLKeyboard

trait StateEventHandling extends ThingStore[Any] {

  abstract override def __update(dt:Float) {
    super.__update(dt)
    val sinks = (everything ++ Traversable(this)) flatMap {
      case s:EventSink => Some(s)
      case _ => None
    }

    for {
      source <- app.eventSources
      sink <- sinks
    } {
      source.presentTo(sink)
    }
  }
}

trait StateMixin extends ScaleneApp with ThingStore[Any] with StateEventHandling with Render { self =>

  def app = this
  val view:View2D

  lazy val startState = new State(this) {
    val view = self.view
    override def everything = self.everything
  }

  def render() {
    if(view!=null) view.__render()
  }

}

abstract class StateIndexed(val app:ScaleneApp) extends StateBase with IndexedThingStore {
  def this(domain:Domain) = this(domain.app)
}

abstract class State(val app:ScaleneApp) extends StateBase with HashedThingStore {
  def this(domain:Domain) = this(domain.app)
}

trait StateBase extends ThingStore[Any] with StateEventHandling with Render {

  def app:ScaleneApp
  val view:View2D

  protected[scalene] def onEnter(bloc: =>Unit) {}
  protected[scalene] def onExit(bloc: =>Unit) {}

  def changeState(state:State) { app.stateMachine.change(state) }
  def pushState(state:State) { app.stateMachine.push(state) }
  def popState() { app.stateMachine.pop() }

  def update(dt:Float) {

  }
  def render() {
    if(view!=null) view.__render()
  }
}

class StateMachine(startState:StateBase) {
  assert(startState != null)
  private val stack = collection.mutable.Stack[StateBase]()

  push(startState)

  def current = {
    assert(!stack.isEmpty)
    stack.top
  }

  def change(s:StateBase) {
    assert(!stack.isEmpty)
    current.onExit()
    stack.pop()
    stack.push(s)
    current.onEnter()
  }

  def push(s:StateBase) {
    stack.push(s)
    current.onEnter()
  }

  def pop() {
    assert(!stack.tail.isEmpty)
    val top = stack.pop()
    top.onExit()
  }
}

object State {

}
