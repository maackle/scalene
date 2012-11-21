package scalene.core

import traits.Render
import actors.Actor
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

trait StateMixin extends ScaleneApp with ThingStore with Render { self =>

  def app = this
  val view:View2D

  lazy val startState = new State(this) {
    val view = self.view
    override def updateables = self.updateables
  }

  def render() {
    if(view!=null) view.__render()
  }
}

abstract class State(val app:ScaleneApp) extends ThingStore with Render {
  def this(domain:Domain) = this(domain.app)
  val view:View2D

  protected def onEnter(bloc: =>Unit) {}
  protected def onExit(bloc: =>Unit) {}

  def render() {
    if(view!=null) view.__render()
  }
}


object State {

  object CrashAndBurn extends State(null.asInstanceOf[ScaleneApp]) {
    val view = null
  }

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