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

abstract class State(val app:ScaleneApp) extends HashedThingStore with StateEventHandling with Render {
  def this(domain:Domain) = this(domain.app)
  val view:View2D

  protected def onEnter(bloc: =>Unit) {}
  protected def onExit(bloc: =>Unit) {}

  def update(dt:Float) { /* typically handled by ThingStore */ }
  def render() {
    if(view!=null) view.__render()
  }
}


object State {

  class StateMachine(startState:State) {
    assert(startState != null)
    private val stack = collection.mutable.Stack[State]()

    push(startState)

    def current = {
      assert(!stack.isEmpty)
      stack.top
    }

    def change(s:State) {
      assert(!stack.isEmpty)
      pop()
      push(s)
    }

    def push(s:State) {
      stack.push(s)
      current.onEnter()
    }

    def pop() {
      assert(!stack.tail.isEmpty)
      val top = stack.pop()
      top.onExit()
    }
  }
}